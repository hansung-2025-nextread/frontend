package com.nextread.readpick.presentation.admin

import com.nextread.readpick.data.model.admin.ReportedReviewDto
import com.nextread.readpick.data.model.admin.ReviewDetailDto
import com.nextread.readpick.data.model.admin.UserDetailDto
import com.nextread.readpick.data.model.admin.UserReviewDto

/**
 * 관리자 대시보드 UI 상태
 */
sealed interface AdminDashboardUiState {
    data object Loading : AdminDashboardUiState
    data class Success(
        val reportedReviews: List<ReportedReviewDto>,
        val selectedReview: ReviewDetailDto? = null,
        val selectedUser: UserDetailDto? = null,
        val userReviews: List<UserReviewDto> = emptyList()
    ) : AdminDashboardUiState
    data class Error(val message: String) : AdminDashboardUiState
}

/**
 * 관리자 대시보드 탭
 */
enum class AdminTab(val title: String) {
    REPORTED_REVIEWS("신고된 리뷰"),
    USER_MANAGEMENT("사용자 관리")
}
