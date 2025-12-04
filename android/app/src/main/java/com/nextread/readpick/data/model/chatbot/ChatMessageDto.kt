package com.nextread.readpick.data.model.chatbot

import com.nextread.readpick.data.model.book.BookDto
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageDto(
    val role: MessageRole,          // USER 또는 ASSISTANT
    val content: String,             // 메시지 내용
    val createdAt: String,           // ISO 8601 형식 (예: "2025-12-01T10:30:00")
    val books: List<BookDto>? = null // AI 응답에만 책 추천 포함 가능
)
