package com.nextread.readpick.data.model.chatbot

import kotlinx.serialization.Serializable

// 기존 대화형 챗봇용 (혹시 나중에 쓸 수도 있으니 남겨둠)
@Serializable
data class ChatMessage(
    val role: String,
    val content: String
)

@Serializable
data class ChatRequest(
    val userMessage: String,
    val conversationHistory: List<ChatMessage> = emptyList()
)
