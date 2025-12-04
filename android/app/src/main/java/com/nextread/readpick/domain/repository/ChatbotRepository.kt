package com.nextread.readpick.domain.repository

import com.nextread.readpick.data.model.chatbot.ChatSessionDetailDto
import com.nextread.readpick.data.model.chatbot.ChatSessionSummaryDto
import com.nextread.readpick.data.model.chatbot.SendMessageResponse

interface ChatbotRepository {

    /**
     * 새 세션 생성
     */
    suspend fun createSession(): Result<ChatSessionDetailDto>

    /**
     * 세션 목록 조회
     */
    suspend fun getSessions(): Result<List<ChatSessionSummaryDto>>

    /**
     * 세션 상세 조회 (메시지 포함)
     */
    suspend fun getSessionDetail(sessionId: Long): Result<ChatSessionDetailDto>

    /**
     * 메시지 전송
     */
    suspend fun sendMessage(
        sessionId: Long,
        message: String
    ): Result<SendMessageResponse>

    /**
     * 세션 삭제
     */
    suspend fun deleteSession(sessionId: Long): Result<Unit>
}
