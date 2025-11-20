package com.nextread.readpick.data.model.admin

import kotlinx.serialization.Serializable

/**
 * 신고된 리뷰 정보
 * 백엔드 ReportedReviewResponse와 매핑
 */
@Serializable
data class ReportedReviewDto(
    val reviewId: Long,
    val content: String,
    val reportCount: Int,
    val status: String,  // ReviewStatus enum
    val createdAt: String,
    // 작성자 정보
    val userId: Long,
    val userName: String,
    val userEmail: String,
    // 도서 정보
    val bookIsbn13: String,
    val bookTitle: String
)
