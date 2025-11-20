package com.nextread.readpick.data.remote.api

import com.nextread.readpick.data.model.chatbot.ChatRequest
import com.nextread.readpick.data.model.common.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatbotApi {

    /**
     * 챗봇과 대화하여 응답을 받음 (서버의 GeminiApiService.generateChatResponse 호출)
     */
    @POST("v1/api/chatbot/chat") // 서버의 챗봇 엔드포인트 경로를 확인하세요.
    suspend fun sendChat(
        @Body request: ChatRequest
    ): ApiResponse<String> // 서버가 최종 응답 텍스트를 String으로 반환한다고 가정

    // (선택 사항)
    /**
     * 의도 분류 요청
     */
    // @POST("v1/api/chatbot/intent")
    // suspend fun classifyIntent(@Body query: String): ApiResponse<SearchType>
}