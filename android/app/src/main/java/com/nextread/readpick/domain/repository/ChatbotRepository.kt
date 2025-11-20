package com.nextread.readpick.domain.repository

import com.nextread.readpick.data.model.chatbot.ChatMessage

interface ChatbotRepository {

    /**
     * 챗봇에게 메시지를 보내고 응답 텍스트를 받음
     */
    suspend fun getChatResponse(
        userMessage: String,
        history: List<ChatMessage>
    ): Result<String>
}