package com.nextread.readpick.presentation.community.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextread.readpick.data.model.community.CommunityCategoryDto
import com.nextread.readpick.data.model.community.CommunityPostDto
import com.nextread.readpick.domain.repository.CommunityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val communityRepository: CommunityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CommunityUiState>(CommunityUiState.Loading)
    val uiState: StateFlow<CommunityUiState> = _uiState.asStateFlow()

    private var currentPage = 0
    private var categories: List<CommunityCategoryDto> = emptyList()
    private var posts: MutableList<CommunityPostDto> = mutableListOf()
    private var selectedCategoryId: Long? = null
    private var sortType: SortType = SortType.LATEST
    private var hasMoreData = true

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = CommunityUiState.Loading

            // 카테고리 로드
            communityRepository.getCategories()
                .onSuccess { categoryList ->
                    categories = categoryList
                    // 게시물 로드
                    loadPosts(reset = true)
                }
                .onFailure { exception ->
                    _uiState.value = CommunityUiState.Error(
                        exception.message ?: "카테고리를 불러올 수 없습니다"
                    )
                }
        }
    }

    private fun loadPosts(reset: Boolean = false) {
        viewModelScope.launch {
            if (reset) {
                currentPage = 0
                posts.clear()
                hasMoreData = true
            }

            val currentState = _uiState.value
            if (currentState is CommunityUiState.Success && !reset) {
                _uiState.value = currentState.copy(isLoadingMore = true)
            }

            communityRepository.getPosts(
                page = currentPage,
                categoryId = selectedCategoryId,
                sort = sortType.value
            ).onSuccess { pageResponse ->
                posts.addAll(pageResponse.content)
                hasMoreData = !pageResponse.last
                currentPage++

                _uiState.value = CommunityUiState.Success(
                    categories = categories,
                    posts = posts.toList(),
                    selectedCategoryId = selectedCategoryId,
                    sortType = sortType,
                    isLoadingMore = false,
                    hasMoreData = hasMoreData
                )
            }.onFailure { exception ->
                if (reset) {
                    _uiState.value = CommunityUiState.Error(
                        exception.message ?: "게시물을 불러올 수 없습니다"
                    )
                } else {
                    // 더 불러오기 실패 시 기존 데이터 유지
                    val state = _uiState.value
                    if (state is CommunityUiState.Success) {
                        _uiState.value = state.copy(isLoadingMore = false)
                    }
                }
            }
        }
    }

    fun selectCategory(categoryId: Long?) {
        selectedCategoryId = categoryId
        loadPosts(reset = true)
    }

    fun changeSortType(newSortType: SortType) {
        sortType = newSortType
        loadPosts(reset = true)
    }

    fun loadMorePosts() {
        val currentState = _uiState.value
        if (currentState is CommunityUiState.Success &&
            !currentState.isLoadingMore &&
            currentState.hasMoreData) {
            loadPosts(reset = false)
        }
    }

    fun refresh() {
        loadPosts(reset = true)
    }
}
