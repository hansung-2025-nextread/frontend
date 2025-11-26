package com.nextread.readpick.data.remote.api

import android.R
import com.nextread.readpick.data.model.chatbot.MessageRequest
import com.nextread.readpick.data.model.chatbot.ChatResponseDto
import com.nextread.readpick.data.model.chatbot.ChatSessionDto
import com.nextread.readpick.data.model.chatbot.CreateSessionResponse
import com.nextread.readpick.data.model.common.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatbotApi {

    //매세지 전송
    @POST("v1/api/chatbot/conversations/sessions/{sessionId}/messages")
    suspend fun sendMessage(
        @Path("sessionId") sessionId: Long,
        @Body request: MessageRequest
    ): ApiResponse<ChatResponseDto>

    //세션 목록 조회
    @GET("v1/api/chatbot/conversations/sessions")
    suspend fun getSessions(): ApiResponse<List<ChatSessionDto>>

    //새 세션 생성
    @POST("/v1/api/chatbot/conversations/sessions")
    suspend fun createSession(): ApiResponse<CreateSessionResponse>
}
