package com.nextread.readpick.data.model.chatbot

import kotlinx.serialization.Serializable

@Serializable
enum class MessageRole {
    USER,       // 사용자 메시지
    ASSISTANT   // AI 챗봇 응답
}
