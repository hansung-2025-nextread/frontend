package com.nextread.readpick.presentation.chatbot

/**
 * 화면에 보여질 말풍선 데이터 (UI용)
 */
data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isFromUser: Boolean
)

/**
 * 화면의 상태를 정의
 */
sealed interface ChatbotUiState {
    data object Loading : ChatbotUiState

    data class Success(
        val messages: List<ChatMessage>,
        val isLoading: Boolean = false
    ) : ChatbotUiState

    data class Error(val message: String) : ChatbotUiState
}