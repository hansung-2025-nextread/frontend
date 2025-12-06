package com.nextread.readpick.data.model.review

import kotlinx.serialization.Serializable

/**
 * 리뷰 신고 요청
 */
@Serializable
data class ReportReviewRequest(
    val reason: String
)
