package com.nextread.readpick.domain.repository

import com.nextread.readpick.data.model.chatbot.ChatResponseDto
import com.nextread.readpick.data.model.chatbot.ChatSessionDto

interface ChatbotRepository {
    /**
     * 1. 사이드바용 세션 목록 가져오기
     * (GET /sessions)
     */
    suspend fun getSessions(): Result<List<ChatSessionDto>>

    /**
     * 2. 새로운 채팅방(세션) 만들기
     * (POST /sessions) -> 성공하면 sessionId(String) 반환
     */
    suspend fun createSession(): Result<Long>
    //메세지 보내고 답변 받기
    suspend fun sendMessage(sessionId: Long, message: String): Result<ChatResponseDto>
}