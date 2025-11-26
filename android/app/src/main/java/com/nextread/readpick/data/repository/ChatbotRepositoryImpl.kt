package com.nextread.readpick.data.repository

import com.nextread.readpick.data.model.chatbot.ChatResponseDto
import com.nextread.readpick.data.model.chatbot.ChatSessionDto
import com.nextread.readpick.data.model.chatbot.MessageRequest
import com.nextread.readpick.data.remote.api.ChatbotApi
import com.nextread.readpick.domain.repository.ChatbotRepository
import javax.inject.Inject

class ChatbotRepositoryImpl @Inject constructor(
    private val chatApi: ChatbotApi
) : ChatbotRepository {
    // 1. 세션 목록 조회 (사이드바)
    override suspend fun getSessions(): Result<List<ChatSessionDto>> = runCatching {
        val response = chatApi.getSessions()

        if (response.success && response.data != null) {
            response.data
        } else {
            throw Exception(response.message ?: "세션 목록을 불러오지 못했습니다.")
        }
    }

    // 2. 새 세션 생성 (새 채팅)
    override suspend fun createSession(): Result<Long> = runCatching {
        val response = chatApi.createSession()

        // 응답에서 sessionId(Long)만 쏙 빼서 반환
        if (response.success && response.data != null) {
            response.data.sessionId
        } else {
            throw Exception(response.message ?: "새 채팅방을 만들지 못했습니다.")
        }
    }
    //메시지 전송 및 답변 받기
    override suspend fun sendMessage(sessionId: Long, message: String): Result<ChatResponseDto> = runCatching {
        // API가 요구하는 Request Body 형태로 포장
        val request = MessageRequest(message = message)

        // API 호출
        val response = chatApi.sendMessage(sessionId, request)

        if (response.success && response.data != null) {
            response.data // 답변(reply)과 추천책(books)이 들어있는 객체 반환
        } else {
            throw Exception(response.message ?: "메시지 전송에 실패했습니다.")
        }
    }

    // 4. 세션 상세 조회 (이전 대화 내용 불러오기) - 필요시 구현
    /*
    override suspend fun getSessionDetail(sessionId: Long): Result<List<ChatMessageDto>> = runCatching {
        val response = chatApi.getSessionDetail(sessionId)
        if (response.success && response.data != null) {
            response.data
        } else {
            throw Exception(response.message ?: "대화 내용을 불러오지 못했습니다.")
        }
    }
    */
}