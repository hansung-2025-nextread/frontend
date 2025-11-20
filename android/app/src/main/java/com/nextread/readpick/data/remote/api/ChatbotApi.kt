package com.nextread.readpick.data.remote.api

import retrofit2.http.Body
import retrofit2.http.POST
import com.nextread.readpick.data.model.chatbot.ChatbotSearchRequest
import com.nextread.readpick.data.model.chatbot.ChatbotSearchResponse

/**
 * 챗봇 API 통신을 위한 Retrofit 서비스 인터페이스
 */
interface ChatbotApi {

    /**
     * 챗봇 검색 API (단일 쿼리 전송)
     * @param request ChatbotSearchRequest (쿼리 문자열 포함)
     * @return ChatbotSearchResponse (메시지 및 도서 목록 포함)
     */
    @POST("v1/api/chatbot/search")
    suspend fun searchChatbot(@Body request: ChatbotSearchRequest): ChatbotSearchResponse
}