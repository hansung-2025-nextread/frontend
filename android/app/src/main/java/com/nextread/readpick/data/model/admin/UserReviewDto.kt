package com.nextread.readpick.data.model.admin

import kotlinx.serialization.Serializable

/**
 * 사용자별 리뷰 정보 (관리자용)
 * 백엔드 ReportedReviewResponse와 동일한 구조
 */
@Serializable
data class UserReviewDto(
    val reviewId: Long,
    val content: String,
    val reportCount: Int,
    val status: String,
    val createdAt: String,
    val userId: Long,
    val userName: String,
    val userEmail: String,
    val bookIsbn13: String,
    val bookTitle: String
) {
    // status가 HIDDEN_BY_ADMIN이면 숨김 처리된 리뷰
    val isHidden: Boolean
        get() = status == "HIDDEN_BY_ADMIN"
}
