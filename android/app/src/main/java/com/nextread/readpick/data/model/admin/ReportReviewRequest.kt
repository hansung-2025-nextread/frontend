package com.nextread.readpick.data.model.admin

import kotlinx.serialization.Serializable

/**
 * 리뷰 신고 요청 (일반 사용자용)
 */
@Serializable
data class ReportReviewRequest(
    val reason: String
)
