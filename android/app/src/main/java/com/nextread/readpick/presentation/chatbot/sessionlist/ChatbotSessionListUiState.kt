package com.nextread.readpick.presentation.chatbot.sessionlist

import com.nextread.readpick.data.model.chatbot.ChatSessionSummaryDto

sealed interface ChatbotSessionListUiState {
    data object Loading : ChatbotSessionListUiState

    data class Success(
        val sessions: List<ChatSessionSummaryDto>
    ) : ChatbotSessionListUiState

    data class Error(val message: String) : ChatbotSessionListUiState
}
