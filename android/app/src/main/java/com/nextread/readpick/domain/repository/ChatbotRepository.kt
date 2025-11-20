package com.nextread.readpick.domain.repository

import com.nextread.readpick.data.model.chatbot.ChatbotSearchResponse

/**
 * 챗봇 관련 데이터 처리를 위한 리포지토리 인터페이스
 */
interface ChatbotRepository {
    /**
     * 챗봇 검색 API (POST /api/chatbot/search)를 호출하여 답변과 도서 목록을 가져옵니다.
     *
     * @param query 사용자 입력 쿼리 (검색어)
     * @return 응답 텍스트와 추천 도서 목록을 포함하는 Result<ChatbotSearchResponse>
     */
    suspend fun searchChatbot(query: String): Result<ChatbotSearchResponse>
}