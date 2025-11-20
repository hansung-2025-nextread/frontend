package com.nextread.readpick.data.repository

import com.nextread.readpick.data.remote.api.ChatbotApi
import com.nextread.readpick.domain.repository.ChatbotRepository
import com.nextread.readpick.data.model.chatbot.ChatbotSearchRequest
import com.nextread.readpick.data.model.chatbot.ChatbotSearchResponse
import javax.inject.Inject

/**
 * ChatbotRepository의 구현체
 */
class ChatbotRepositoryImpl @Inject constructor(
    private val apiService: ChatbotApi
) : ChatbotRepository {

    /**
     * 새로운 API 명세에 따라 ChatbotSearchRequest를 생성하여 API를 호출합니다.
     */
    override suspend fun searchChatbot(query: String): Result<ChatbotSearchResponse> {
        return try {
            val request = ChatbotSearchRequest(query = query)
            val response = apiService.searchChatbot(request)
            // 응답이 성공적으로 왔다면 Result.success 반환
            Result.success(response)
        } catch (e: Exception) {
            // 통신 오류, 파싱 오류 등 예외 발생 시 Result.failure 반환
            Result.failure(e)
        }
    }
}