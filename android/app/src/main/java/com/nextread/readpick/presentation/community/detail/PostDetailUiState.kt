package com.nextread.readpick.presentation.community.detail

import com.nextread.readpick.data.model.community.CommunityCommentDto
import com.nextread.readpick.data.model.community.CommunityPostDto

sealed interface PostDetailUiState {
    data object Loading : PostDetailUiState
    data class Success(
        val post: CommunityPostDto,
        val comments: List<CommunityCommentDto>,
        val isLikeLoading: Boolean = false,
        val isCommentLoading: Boolean = false
    ) : PostDetailUiState
    data class Error(val message: String) : PostDetailUiState
}
