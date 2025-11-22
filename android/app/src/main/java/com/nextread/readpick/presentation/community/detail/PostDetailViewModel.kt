package com.nextread.readpick.presentation.community.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextread.readpick.data.local.TokenManager
import com.nextread.readpick.data.model.community.CommunityCommentDto
import com.nextread.readpick.data.model.community.CommunityPostDto
import com.nextread.readpick.domain.repository.CommunityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "PostDetailViewModel"

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val communityRepository: CommunityRepository,
    private val tokenManager: TokenManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val postId: Long = savedStateHandle.get<String>("postId")?.toLongOrNull() ?: 0L

    // 현재 로그인한 사용자 ID
    val currentUserId: Long? = tokenManager.getUserId()

    private val _uiState = MutableStateFlow<PostDetailUiState>(PostDetailUiState.Loading)
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    // 게시글 삭제 완료 이벤트
    private val _postDeletedEvent = MutableSharedFlow<Unit>()
    val postDeletedEvent = _postDeletedEvent.asSharedFlow()

    private var post: CommunityPostDto? = null
    private var comments: MutableList<CommunityCommentDto> = mutableListOf()

    init {
        loadPostDetail()
    }

    private fun loadPostDetail() {
        viewModelScope.launch {
            _uiState.value = PostDetailUiState.Loading

            communityRepository.getPostDetail(postId)
                .onSuccess { postDto ->
                    post = postDto
                    loadComments()
                }
                .onFailure { exception ->
                    _uiState.value = PostDetailUiState.Error(
                        exception.message ?: "게시물을 불러올 수 없습니다"
                    )
                }
        }
    }

    private fun loadComments() {
        viewModelScope.launch {
            communityRepository.getComments(postId)
                .onSuccess { commentList ->
                    comments = commentList.toMutableList()
                    post?.let {
                        _uiState.value = PostDetailUiState.Success(
                            post = it,
                            comments = comments.toList()
                        )
                    }
                }
                .onFailure {
                    post?.let {
                        _uiState.value = PostDetailUiState.Success(
                            post = it,
                            comments = emptyList()
                        )
                    }
                }
        }
    }

    fun toggleLike() {
        val currentState = _uiState.value
        if (currentState !is PostDetailUiState.Success) return

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLikeLoading = true)

            val currentPost = currentState.post
            val result = if (currentPost.liked) {
                communityRepository.unlikePost(postId)
            } else {
                communityRepository.likePost(postId)
            }

            result.onSuccess {
                val updatedPost = currentPost.copy(
                    liked = !currentPost.liked,
                    likeCount = if (currentPost.liked) {
                        currentPost.likeCount - 1
                    } else {
                        currentPost.likeCount + 1
                    }
                )
                post = updatedPost
                val latestState = _uiState.value
                if (latestState is PostDetailUiState.Success) {
                    _uiState.value = latestState.copy(
                        post = updatedPost,
                        isLikeLoading = false
                    )
                }
            }.onFailure {
                val latestState = _uiState.value
                if (latestState is PostDetailUiState.Success) {
                    _uiState.value = latestState.copy(isLikeLoading = false)
                }
            }
        }
    }

    fun addComment(content: String) {
        val currentState = _uiState.value
        if (currentState !is PostDetailUiState.Success) return
        if (content.isBlank()) return

        viewModelScope.launch {
            _uiState.value = currentState.copy(isCommentLoading = true)

            communityRepository.createComment(postId, content)
                .onSuccess { newComment ->
                    comments.add(newComment)
                    val latestState = _uiState.value
                    if (latestState is PostDetailUiState.Success) {
                        val updatedPost = latestState.post.copy(
                            commentCount = latestState.post.commentCount + 1
                        )
                        post = updatedPost
                        _uiState.value = latestState.copy(
                            post = updatedPost,
                            comments = comments.toList(),
                            isCommentLoading = false
                        )
                    }
                }
                .onFailure {
                    val latestState = _uiState.value
                    if (latestState is PostDetailUiState.Success) {
                        _uiState.value = latestState.copy(isCommentLoading = false)
                    }
                }
        }
    }

    fun deleteComment(commentId: Long) {
        val currentState = _uiState.value
        if (currentState !is PostDetailUiState.Success) return

        viewModelScope.launch {
            communityRepository.deleteComment(postId, commentId)
                .onSuccess {
                    comments.removeAll { it.id == commentId }
                    val updatedPost = currentState.post.copy(
                        commentCount = currentState.post.commentCount - 1
                    )
                    post = updatedPost
                    _uiState.value = currentState.copy(
                        post = updatedPost,
                        comments = comments.toList()
                    )
                }
        }
    }

    fun deletePost() {
        val currentState = _uiState.value
        if (currentState !is PostDetailUiState.Success) return

        viewModelScope.launch {
            communityRepository.deletePost(postId)
                .onSuccess {
                    _postDeletedEvent.emit(Unit)
                }
                .onFailure { exception ->
                    Log.e(TAG, "게시글 삭제 실패", exception)
                }
        }
    }

    fun refresh() {
        loadPostDetail()
    }
}
