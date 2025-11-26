package com.nextread.readpick.presentation.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextread.readpick.data.model.chatbot.ChatBookDto
import com.nextread.readpick.data.model.chatbot.ChatMessageDto
import com.nextread.readpick.data.model.chatbot.ChatResponseDto
import com.nextread.readpick.data.model.chatbot.ChatSessionDto
import com.nextread.readpick.domain.repository.ChatbotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatbotViewModel @Inject constructor(
    private val chatRepository: ChatbotRepository // 💉 Repository 주입!
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChatbotUiState>(ChatbotUiState.Loading)
    val uiState: StateFlow<ChatbotUiState> = _uiState.asStateFlow()

    init {
        // 화면이 켜지자마자 사이드바(세션 목록)를 불러옵니다.
        loadSessions()
    }

    // 📂 사이드바 세션 목록 불러오기
    private fun loadSessions() {
        viewModelScope.launch {
            chatRepository.getSessions()
                .onSuccess { sessionList ->
                    _uiState.update { state ->
                        // 기존 상태가 Success면 내용만 업데이트, 아니면 새로 Success 생성
                        if (state is ChatbotUiState.Success) {
                            state.copy(sessions = sessionList)
                        } else {
                            ChatbotUiState.Success(sessions = sessionList)
                        }
                    }
                }
                .onFailure {
                    // 실패해도 일단 빈 화면(Success)으로 보여주되, 로그나 에러 처리는 필요
                    _uiState.value = ChatbotUiState.Success(sessions = emptyList())
                }
        }
    }

    // 🚀 메시지 전송 (핵심 로직)
    fun sendMessage(text: String) {
        if (text.isBlank()) return

        // 현재 상태가 Success일 때만 동작
        val currentState = _uiState.value as? ChatbotUiState.Success ?: return

        viewModelScope.launch {
            // 1. [UI 업데이트] 사용자가 보낸 메시지를 먼저 화면에 표시 (즉각 반응)
            val userMessage = ChatMessageDto(
                messageId = UUID.randomUUID().toString(), // 임시 ID
                sender = "USER",
                content = text,
                timestamp = "Now"
            )

            _uiState.update {
                currentState.copy(
                    messages = currentState.messages + userMessage,
                    isAiTyping = true // 로딩 시작
                )
            }

            // 2. [세션 확인] 현재 방 번호(ID)가 없으면 새로 만듦
            val sessionId = currentState.currentSessionId ?: createNewSession()

            if (sessionId == null) {
                _uiState.value = ChatbotUiState.Error("채팅방을 생성할 수 없습니다.")
                return@launch
            }

            // 3. [API 호출] 메시지 전송하고 AI 답변 받기
            chatRepository.sendMessage(sessionId, text)
                .onSuccess { response ->
                    // 4. [UI 업데이트] AI의 답변을 화면에 추가
                    val aiMessage = ChatMessageDto(
                        messageId = UUID.randomUUID().toString(),
                        sender = "AI",
                        content = response.reply, // AI의 답변 텍스트
                        timestamp = "Now"
                    )

                    // TODO: response.books (추천 도서 목록)도 UI에 보여주고 싶다면 여기서 처리

                    _uiState.update { state ->
                        if (state is ChatbotUiState.Success) {
                            state.copy(
                                currentSessionId = sessionId, // 세션 ID 확정
                                messages = state.messages + aiMessage,
                                isAiTyping = false, // 로딩 끝
                                // 첫 대화였다면 사이드바 목록 갱신 필요 (선택사항)
                            )
                        } else state
                    }

                    // 첫 대화였다면 사이드바 목록을 다시 불러옴
                    if (currentState.currentSessionId == null) {
                        loadSessions()
                    }
                }
                .onFailure { exception ->
                    // 실패 시 에러 처리 (여기선 로딩만 끔)
                    _uiState.update {
                        (it as ChatbotUiState.Success).copy(isAiTyping = false)
                    }
                }
        }
    }

    // 내부 함수: 새 세션 만들기
    private suspend fun createNewSession(): Long? {
        return chatRepository.createSession().getOrNull()
    }

    // ➕ 새 채팅 시작하기 버튼
    fun startNewChat() {
        _uiState.update { state ->
            if (state is ChatbotUiState.Success) {
                state.copy(currentSessionId = null, messages = emptyList())
            } else state
        }
    }

    // 📑 사이드바에서 기존 채팅방 클릭
    fun selectSession(sessionId: Long) {
        viewModelScope.launch {
            // TODO: getSessionDetail API를 구현했다면 여기서 호출하여 대화 내역 불러오기
            // 지금은 임시로 방만 바꿈
            _uiState.update { state ->
                if (state is ChatbotUiState.Success) {
                    state.copy(currentSessionId = sessionId, messages = emptyList())
                } else state
            }
        }
    }
}