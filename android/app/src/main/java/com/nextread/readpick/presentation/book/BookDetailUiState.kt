package com.nextread.readpick.presentation.book

import com.nextread.readpick.data.model.book.BookDetailDto
import com.nextread.readpick.data.model.review.ReviewDto
import com.nextread.readpick.data.model.search.PageInfo

/**
 * 책 상세 화면 UI 상태
 */
sealed interface BookDetailUiState {
    /**
     * 로딩 중
     */
    data object Loading : BookDetailUiState

    /**
     * 성공 - 책 정보 및 리뷰 로드 완료
     */
    data class Success(
        val book: BookDetailDto,
        val isSaved: Boolean = false,
        val reviews: List<ReviewDto> = emptyList(),
        val myReview: ReviewDto? = null,
        val reviewsPage: PageInfo = PageInfo(),

        // 특정 작업에 대한 로딩 상태
        val isSaveLoading: Boolean = false,
        val isReviewLoading: Boolean = false,
        val isReviewsLoadingMore: Boolean = false
    ) : BookDetailUiState

    /**
     * 에러
     */
    data class Error(val message: String) : BookDetailUiState
}
