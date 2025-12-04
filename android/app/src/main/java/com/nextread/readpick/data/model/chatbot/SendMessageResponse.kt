package com.nextread.readpick.data.model.chatbot

import com.nextread.readpick.data.model.book.BookDto
import kotlinx.serialization.Serializable

@Serializable
data class SendMessageResponse(
    val sessionId: Long,
    val reply: String,          // AI의 응답 텍스트
    val books: List<BookDto>    // 추천 도서 목록
)
