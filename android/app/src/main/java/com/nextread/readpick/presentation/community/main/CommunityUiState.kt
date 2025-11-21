package com.nextread.readpick.presentation.community.main

import com.nextread.readpick.data.model.community.CommunityCategoryDto
import com.nextread.readpick.data.model.community.CommunityPostDto

sealed interface CommunityUiState {
    data object Loading : CommunityUiState
    data class Success(
        val categories: List<CommunityCategoryDto>,
        val posts: List<CommunityPostDto>,
        val selectedCategoryId: Long?,
        val sortType: SortType,
        val isLoadingMore: Boolean = false,
        val hasMoreData: Boolean = true
    ) : CommunityUiState
    data class Error(val message: String) : CommunityUiState
}

enum class SortType(val value: String, val displayName: String) {
    LATEST("latest", "최신순"),
    POPULAR("popular", "인기순")
}
