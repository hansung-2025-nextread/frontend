package com.nextread.readpick.data.repository

import android.util.Log
import com.nextread.readpick.data.model.chatbot.ChatMessage
import com.nextread.readpick.data.model.chatbot.ChatRequest
import com.nextread.readpick.data.remote.api.ChatbotApi
import com.nextread.readpick.domain.repository.ChatbotRepository
import javax.inject.Inject

class ChatbotRepositoryImpl @Inject constructor(
    private val chatbotApi: ChatbotApi
) : ChatbotRepository {

    override suspend fun getChatResponse(
        userMessage: String,
        history: List<ChatMessage>
    ): Result<String> = runCatching {
        Log.d(TAG, "챗봇 API 호출: $userMessage")

        val request = ChatRequest(userMessage, history)
        val response = chatbotApi.sendChat(request)

        if (response.success && response.data != null) {
            Log.d(TAG, "챗봇 응답 성공")
            response.data
        } else {
            Log.e(TAG, "챗봇 응답 실패: ${response.message}")
            throw Exception(response.message ?: "챗봇 응답을 받지 못했습니다")
        }
    }.onFailure { exception ->
        Log.e(TAG, "챗봇 API 호출 에러", exception)
    }

    companion object {
        private const val TAG = "ChatbotRepository"
    }
}