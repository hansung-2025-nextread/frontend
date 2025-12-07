package com.nextread.readpick.data.model.review

import kotlinx.serialization.Serializable

/**
 * 리뷰 작성/수정 요청
 */
@Serializable
data class ReviewRequest(
    val content: String
)
