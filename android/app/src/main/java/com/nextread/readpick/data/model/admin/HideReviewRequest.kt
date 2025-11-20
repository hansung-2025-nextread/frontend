package com.nextread.readpick.data.model.admin

import kotlinx.serialization.Serializable

/**
 * 리뷰 숨김 요청
 */
@Serializable
data class HideReviewRequest(
    val reason: String
)
