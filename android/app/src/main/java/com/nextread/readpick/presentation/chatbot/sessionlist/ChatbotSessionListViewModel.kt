package com.nextread.readpick.presentation.chatbot.sessionlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextread.readpick.domain.repository.ChatbotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatbotSessionListViewModel @Inject constructor(
    private val chatbotRepository: ChatbotRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChatbotSessionListUiState>(
        ChatbotSessionListUiState.Loading
    )
    val uiState: StateFlow<ChatbotSessionListUiState> = _uiState.asStateFlow()

    init {
        loadSessions()
    }

    fun loadSessions() {
        _uiState.value = ChatbotSessionListUiState.Loading

        viewModelScope.launch {
            chatbotRepository.getSessions()
                .onSuccess { sessions ->
                    _uiState.value = ChatbotSessionListUiState.Success(sessions)
                }
                .onFailure { exception ->
                    _uiState.value = ChatbotSessionListUiState.Error(
                        exception.message ?: "세션 목록을 불러올 수 없습니다"
                    )
                }
        }
    }

    /**
     * 새 세션 생성 후 채팅 화면으로 이동
     */
    fun createNewSession(onSessionCreated: (Long) -> Unit) {
        viewModelScope.launch {
            chatbotRepository.createSession()
                .onSuccess { session ->
                    onSessionCreated(session.id)
                    loadSessions() // 목록 새로고침
                }
                .onFailure { exception ->
                    _uiState.value = ChatbotSessionListUiState.Error(
                        exception.message ?: "세션을 생성할 수 없습니다"
                    )
                }
        }
    }

    /**
     * 세션 삭제
     */
    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            chatbotRepository.deleteSession(sessionId)
                .onSuccess {
                    loadSessions() // 삭제 후 목록 새로고침
                }
                .onFailure { exception ->
                    _uiState.value = ChatbotSessionListUiState.Error(
                        exception.message ?: "세션을 삭제할 수 없습니다"
                    )
                }
        }
    }
}
