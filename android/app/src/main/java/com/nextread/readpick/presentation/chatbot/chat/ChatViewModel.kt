package com.nextread.readpick.presentation.chatbot.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextread.readpick.data.model.chatbot.ChatMessageDto
import com.nextread.readpick.data.model.chatbot.MessageRole
import com.nextread.readpick.domain.repository.ChatbotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatbotRepository: ChatbotRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // 네비게이션 파라미터에서 sessionId 추출 (String으로 전달되므로 Long으로 변환)
    private val sessionId: Long = checkNotNull(savedStateHandle.get<String>("sessionId")).toLong()

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadSession()
    }

    private fun loadSession() {
        _uiState.value = ChatUiState.Loading

        viewModelScope.launch {
            chatbotRepository.getSessionDetail(sessionId)
                .onSuccess { session ->
                    _uiState.value = ChatUiState.Success(
                        sessionId = session.id,
                        sessionTitle = session.title,
                        messages = session.messages
                    )
                }
                .onFailure { exception ->
                    _uiState.value = ChatUiState.Error(
                        exception.message ?: "세션을 불러올 수 없습니다"
                    )
                }
        }
    }

    /**
     * 메시지 전송
     */
    fun sendMessage(message: String) {
        val currentState = _uiState.value
        if (currentState !is ChatUiState.Success || message.isBlank()) return

        // 1. 사용자 메시지를 UI에 즉시 추가 (낙관적 업데이트)
        val userMessage = ChatMessageDto(
            role = MessageRole.USER,
            content = message,
            createdAt = Instant.now().toString(),
            books = null
        )

        _uiState.value = currentState.copy(
            messages = currentState.messages + userMessage,
            isSending = true  // 로딩 표시
        )

        // 2. API 호출
        viewModelScope.launch {
            chatbotRepository.sendMessage(sessionId, message)
                .onSuccess { response ->
                    // AI 응답을 UI에 추가
                    val assistantMessage = ChatMessageDto(
                        role = MessageRole.ASSISTANT,
                        content = response.reply,
                        createdAt = Instant.now().toString(),
                        books = response.books
                    )

                    val state = _uiState.value
                    if (state is ChatUiState.Success) {
                        _uiState.value = state.copy(
                            messages = state.messages + assistantMessage,
                            isSending = false
                        )
                    }
                }
                .onFailure { exception ->
                    // 실패 시 로딩 상태만 해제 (사용자 메시지는 유지)
                    val state = _uiState.value
                    if (state is ChatUiState.Success) {
                        _uiState.value = state.copy(isSending = false)
                    }
                    // TODO: 에러 메시지 표시 (SnackBar 또는 Toast)
                }
        }
    }

    fun refresh() {
        loadSession()
    }
}
