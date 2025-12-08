package com.nextread.readpick.presentation.mypage

import com.nextread.readpick.domain.model.Review
import com.nextread.readpick.domain.model.Book

data class MyReviewsUiState(
    val reviews: List<Review> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
