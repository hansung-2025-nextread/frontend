package com.nextread.readpick.data.model.chatbot

import kotlinx.serialization.Serializable

@Serializable
data class ChatSessionSummaryDto(
    val id: Long,
    val title: String,      // 예: "소설 추천 문의"
    val createdAt: String,
    val updatedAt: String
)
