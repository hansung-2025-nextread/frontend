package com.nextread.readpick.data.model.chatbot

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val role: String, // "user" 또는 "assistant"
    val content: String
)

@Serializable
data class ChatRequest(
    val userMessage: String,
    val conversationHistory: List<ChatMessage> = emptyList()
)