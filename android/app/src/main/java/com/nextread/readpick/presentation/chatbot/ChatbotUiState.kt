package com.nextread.readpick.presentation.chatbot

import com.nextread.readpick.data.model.chatbot.ChatMessageDto
import com.nextread.readpick.data.model.chatbot.ChatResponseDto
import com.nextread.readpick.data.model.chatbot.ChatSessionDto

sealed interface ChatbotUiState {
    data object Loading : ChatbotUiState
    data class Success(
        val currentSessionId: Long? = null,          // 현재 채팅방 ID (없으면 null)
        val sessions: List<ChatSessionDto> = emptyList(), // 사이드바 목록
        val messages: List<ChatMessageDto> = emptyList(), // 채팅 대화 내용
        val isAiTyping: Boolean = false              // AI가 생각중인지 여부
    ) : ChatbotUiState
    data class Error(val message: String) : ChatbotUiState
}