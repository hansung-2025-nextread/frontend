package com.nextread.readpick.data.model.review

import kotlinx.serialization.Serializable

/**
 * 리뷰 목록 페이지 응답
 */
@Serializable
data class ReviewPageResponse(
    val content: List<ReviewDto> = emptyList(),
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val size: Int = 20,
    val number: Int = 0,
    val first: Boolean = true,
    val last: Boolean = true,
    val empty: Boolean = true
)
