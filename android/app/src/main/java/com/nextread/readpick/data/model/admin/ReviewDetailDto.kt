package com.nextread.readpick.data.model.admin

import kotlinx.serialization.Serializable

/**
 * 리뷰 상세 정보 (관리자용)
 * 백엔드 ReviewDetailResponse와 매핑
 */
@Serializable
data class ReviewDetailDto(
    val reviewId: Long,
    val content: String,
    val reportCount: Int,
    val status: String,
    val createdAt: String,
    // 작성자 정보
    val userId: Long,
    val userName: String,
    val userEmail: String,
    // 도서 정보
    val bookIsbn13: String,
    val bookTitle: String,
    // 신고 상세 목록
    val reports: List<ReporterDto> = emptyList()
)

@Serializable
data class ReporterDto(
    val reporterId: Long,
    val reporterName: String,
    val reporterEmail: String,
    val reason: String,
    val reportedAt: String
)
