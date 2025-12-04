package com.nextread.readpick.presentation.chatbot.chat

import com.nextread.readpick.data.model.chatbot.ChatMessageDto

sealed interface ChatUiState {
    data object Loading : ChatUiState

    data class Success(
        val sessionId: Long,
        val sessionTitle: String,
        val messages: List<ChatMessageDto>,
        val isSending: Boolean = false  // 메시지 전송 중 로딩 표시
    ) : ChatUiState

    data class Error(val message: String) : ChatUiState
}
