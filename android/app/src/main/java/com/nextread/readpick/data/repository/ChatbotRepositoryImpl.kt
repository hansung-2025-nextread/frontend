package com.nextread.readpick.data.repository

import android.util.Log
import com.nextread.readpick.data.model.chatbot.ChatSessionDetailDto
import com.nextread.readpick.data.model.chatbot.ChatSessionSummaryDto
import com.nextread.readpick.data.model.chatbot.SendMessageRequest
import com.nextread.readpick.data.model.chatbot.SendMessageResponse
import com.nextread.readpick.data.remote.api.ChatbotApi
import com.nextread.readpick.domain.repository.ChatbotRepository
import javax.inject.Inject

class ChatbotRepositoryImpl @Inject constructor(
    private val chatbotApi: ChatbotApi
) : ChatbotRepository {

    override suspend fun createSession(): Result<ChatSessionDetailDto> = runCatching {
        Log.d(TAG, "새 대화 세션 생성 API 호출")
        val response = chatbotApi.createSession()

        if (response.success && response.data != null) {
            Log.d(TAG, "세션 생성 성공: ${response.data.id}")
            response.data
        } else {
            throw Exception(response.message ?: "세션을 생성할 수 없습니다")
        }
    }.onFailure { exception ->
        Log.e(TAG, "세션 생성 에러", exception)
    }

    override suspend fun getSessions(): Result<List<ChatSessionSummaryDto>> = runCatching {
        Log.d(TAG, "대화 세션 목록 조회 API 호출")
        val response = chatbotApi.getSessions()

        if (response.success && response.data != null) {
            Log.d(TAG, "세션 목록 조회 성공: ${response.data.size}건")
            response.data
        } else {
            throw Exception(response.message ?: "세션 목록을 불러올 수 없습니다")
        }
    }.onFailure { exception ->
        Log.e(TAG, "세션 목록 조회 에러", exception)
    }

    override suspend fun getSessionDetail(sessionId: Long): Result<ChatSessionDetailDto> = runCatching {
        Log.d(TAG, "세션 상세 조회 API 호출: $sessionId")
        val response = chatbotApi.getSessionDetail(sessionId)

        if (response.success && response.data != null) {
            Log.d(TAG, "세션 상세 조회 성공: ${response.data.messages.size}개 메시지")
            response.data
        } else {
            throw Exception(response.message ?: "세션을 불러올 수 없습니다")
        }
    }.onFailure { exception ->
        Log.e(TAG, "세션 상세 조회 에러", exception)
    }

    override suspend fun sendMessage(
        sessionId: Long,
        message: String
    ): Result<SendMessageResponse> = runCatching {
        Log.d(TAG, "메시지 전송 API 호출: $message")
        val request = SendMessageRequest(message)
        val response = chatbotApi.sendMessage(sessionId, request)

        if (response.success && response.data != null) {
            Log.d(TAG, "메시지 전송 성공, 책 추천: ${response.data.books.size}권")
            response.data
        } else {
            throw Exception(response.message ?: "메시지를 전송할 수 없습니다")
        }
    }.onFailure { exception ->
        Log.e(TAG, "메시지 전송 에러", exception)
    }

    override suspend fun deleteSession(sessionId: Long): Result<Unit> = runCatching {
        Log.d(TAG, "세션 삭제 API 호출: $sessionId")
        val response = chatbotApi.deleteSession(sessionId)

        if (response.success) {
            Log.d(TAG, "세션 삭제 성공")
            Unit
        } else {
            throw Exception(response.message ?: "세션을 삭제할 수 없습니다")
        }
    }.onFailure { exception ->
        Log.e(TAG, "세션 삭제 에러", exception)
    }

    companion object {
        private const val TAG = "ChatbotRepository"
    }
}
