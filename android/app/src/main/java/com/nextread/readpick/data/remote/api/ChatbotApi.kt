package com.nextread.readpick.data.remote.api

import com.nextread.readpick.data.model.chatbot.ChatSessionDetailDto
import com.nextread.readpick.data.model.chatbot.ChatSessionSummaryDto
import com.nextread.readpick.data.model.chatbot.SendMessageRequest
import com.nextread.readpick.data.model.chatbot.SendMessageResponse
import com.nextread.readpick.data.model.common.ApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatbotApi {

    /**
     * 새 대화 세션 생성
     */
    @POST("v1/api/chatbot/conversations/sessions")
    suspend fun createSession(): ApiResponse<ChatSessionDetailDto>

    /**
     * 전체 세션 목록 조회
     */
    @GET("v1/api/chatbot/conversations/sessions")
    suspend fun getSessions(): ApiResponse<List<ChatSessionSummaryDto>>

    /**
     * 특정 세션 상세 정보 조회 (메시지 포함)
     */
    @GET("v1/api/chatbot/conversations/sessions/{sessionId}")
    suspend fun getSessionDetail(
        @Path("sessionId") sessionId: Long
    ): ApiResponse<ChatSessionDetailDto>

    /**
     * 세션에 메시지 전송
     */
    @POST("v1/api/chatbot/conversations/sessions/{sessionId}/messages")
    suspend fun sendMessage(
        @Path("sessionId") sessionId: Long,
        @Body request: SendMessageRequest
    ): ApiResponse<SendMessageResponse>

    /**
     * 세션 삭제
     */
    @DELETE("v1/api/chatbot/conversations/sessions/{sessionId}")
    suspend fun deleteSession(
        @Path("sessionId") sessionId: Long
    ): ApiResponse<Unit>
}
