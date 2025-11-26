package com.nextread.readpick.presentation.community.profile

import com.nextread.readpick.data.model.community.CommunityPostDto
import com.nextread.readpick.data.model.community.CommunityUserDto

sealed interface UserProfileUiState {
    data object Loading : UserProfileUiState
    data class Success(
        val user: CommunityUserDto,
        val posts: List<CommunityPostDto>,
        val isLoadingMore: Boolean = false,
        val hasMoreData: Boolean = true
    ) : UserProfileUiState
    data class Error(val message: String) : UserProfileUiState
}
