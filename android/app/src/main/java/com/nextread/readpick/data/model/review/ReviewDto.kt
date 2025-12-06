package com.nextread.readpick.data.model.review

import kotlinx.serialization.Serializable

/**
 * 리뷰 응답 DTO
 */
@Serializable
data class ReviewDto(
    val id: Long,
    val userName: String,
    val userPicture: String,
    val content: String,
    val createdAt: String,
    val updatedAt: String,
    // 내 리뷰 목록 조회 시 포함되는 책 정보
    val isbn13: String? = null,
    val bookTitle: String? = null,
    val bookCover: String? = null
)
