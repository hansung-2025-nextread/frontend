package com.nextread.readpick.data.model.chatbot

import kotlinx.serialization.Serializable

@Serializable
data class ChatSessionDetailDto(
    val id: Long,
    val title: String,
    val messages: List<ChatMessageDto> = emptyList(),  // 전체 대화 내역 (세션 생성 시 비어있을 수 있음)
    val createdAt: String
)
