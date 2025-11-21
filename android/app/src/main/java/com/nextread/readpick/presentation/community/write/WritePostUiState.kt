package com.nextread.readpick.presentation.community.write

import com.nextread.readpick.data.model.book.SavedBookDto
import com.nextread.readpick.data.model.community.CommunityCategoryDto
import com.nextread.readpick.data.model.community.CommunityBookDto

sealed interface WritePostUiState {
    data object Loading : WritePostUiState
    data class Ready(
        val categories: List<CommunityCategoryDto>,
        val savedBooks: List<SavedBookDto> = emptyList(),
        val selectedCategoryId: Long? = null,
        val selectedPostType: PostType? = null,
        val title: String = "",
        val content: String = "",
        val selectedBook: CommunityBookDto? = null,
        val isSubmitting: Boolean = false,
        val errorMessage: String? = null
    ) : WritePostUiState
    data class Success(val postId: Long) : WritePostUiState
    data class Error(val message: String) : WritePostUiState
}

enum class PostType(val value: String, val displayName: String) {
    REVIEW("REVIEW", "후기"),
    QUESTION("QUESTION", "질문"),
    DISCUSSION("DISCUSSION", "자유토론")
}
