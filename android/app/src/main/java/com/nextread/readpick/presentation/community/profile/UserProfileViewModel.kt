package com.nextread.readpick.presentation.community.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextread.readpick.data.model.community.CommunityPostDto
import com.nextread.readpick.data.model.community.CommunityUserDto
import com.nextread.readpick.domain.repository.CommunityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val communityRepository: CommunityRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: Long = savedStateHandle.get<String>("userId")?.toLongOrNull() ?: 0L

    private val _uiState = MutableStateFlow<UserProfileUiState>(UserProfileUiState.Loading)
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    private var user: CommunityUserDto? = null
    private var posts: MutableList<CommunityPostDto> = mutableListOf()
    private var currentPage = 0
    private var hasMoreData = true
    private var totalPosts: Int = 0

    init {
        loadUserPosts(reset = true)
    }

    private fun loadUserPosts(reset: Boolean = false) {
        viewModelScope.launch {
            if (reset) {
                currentPage = 0
                posts.clear()
                hasMoreData = true
                _uiState.value = UserProfileUiState.Loading
            }

            val currentState = _uiState.value
            if (currentState is UserProfileUiState.Success && !reset) {
                _uiState.value = currentState.copy(isLoadingMore = true)
            }

            communityRepository.getUserPosts(userId, page = currentPage)
                .onSuccess { response ->
                    val pageResponse = response.posts
                    posts.addAll(pageResponse.content)
                    hasMoreData = !pageResponse.last
                    totalPosts = pageResponse.totalElements.toInt()
                    currentPage++

                    // 첫 번째 게시글에서 사용자 정보 추출 + readBooksCount 설정
                    if (user == null && posts.isNotEmpty()) {
                        user = CommunityUserDto.fromPost(posts.first(), totalPosts).copy(
                            readBooksCount = response.readBooksCount
                        )
                    } else if (user != null) {
                        // 게시글 수 및 읽은 책 수 업데이트
                        user = user!!.copy(
                            postsCount = totalPosts,
                            readBooksCount = response.readBooksCount
                        )
                    } else {
                        // 게시글이 없는 경우에도 readBooksCount 설정
                        user = CommunityUserDto(
                            id = userId,
                            nickname = "사용자",
                            profileImage = null,
                            readBooksCount = response.readBooksCount,
                            postsCount = totalPosts
                        )
                    }

                    val userInfo = user!!

                    _uiState.value = UserProfileUiState.Success(
                        user = userInfo,
                        posts = posts.toList(),
                        isLoadingMore = false,
                        hasMoreData = hasMoreData
                    )
                }
                .onFailure { exception ->
                    if (reset) {
                        _uiState.value = UserProfileUiState.Error(
                            exception.message ?: "게시물을 불러올 수 없습니다"
                        )
                    } else {
                        val userInfo = user ?: CommunityUserDto(
                            id = userId,
                            nickname = "사용자",
                            profileImage = null,
                            postsCount = totalPosts
                        )
                        _uiState.value = UserProfileUiState.Success(
                            user = userInfo,
                            posts = posts.toList(),
                            isLoadingMore = false,
                            hasMoreData = false
                        )
                    }
                }
        }
    }

    fun loadMorePosts() {
        val currentState = _uiState.value
        if (currentState is UserProfileUiState.Success &&
            !currentState.isLoadingMore &&
            currentState.hasMoreData) {
            loadUserPosts(reset = false)
        }
    }

    fun refresh() {
        user = null
        loadUserPosts(reset = true)
    }
}
