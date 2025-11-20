package com.nextread.readpick.presentation.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.nextread.readpick.domain.repository.ChatbotRepository
import com.nextread.readpick.data.model.chatbot.ChatbotSearchResponse

// ★ UI용 ChatMessage를 'UiChatMessage'라는 별칭으로 부릅니다. (충돌 방지)
import com.nextread.readpick.presentation.chatbot.ChatMessage as UiChatMessage

@HiltViewModel
class ChatbotViewModel @Inject constructor(
    private val chatbotRepository: ChatbotRepository
) : ViewModel() {

    // ★ 중요: 초기 상태를 Loading이 아니라 Success로 설정합니다.
    // 이렇게 하면 앱을 켜자마자 로딩 없이 바로 채팅창이 뜹니다.
    private val _uiState = MutableStateFlow<ChatbotUiState>(
        ChatbotUiState.Success(
            messages = listOf(
                UiChatMessage(
                    text = "Next Read에 오신 것을 환영합니다! 원하는 책을 추천받아 보세요.",
                    isFromUser = false
                )
            ),
            isLoading = false
        )
    )
    val uiState: StateFlow<ChatbotUiState> = _uiState.asStateFlow()

    // init 블록은 삭제했습니다. (위의 초기값 설정으로 대체됨)

    fun sendMessage(text: String) {
        val currentState = _uiState.value

        // 로딩 중이거나 에러 상태면 중단
        if (currentState !is ChatbotUiState.Success || currentState.isLoading) {
            return
        }

        // 1. 사용자 메시지 UI에 추가 (UiChatMessage 사용)
        val userMessage = UiChatMessage(text = text, isFromUser = true)
        val currentMessages = currentState.messages

        _uiState.update {
            if (it is ChatbotUiState.Success) {
                it.copy(messages = currentMessages + userMessage, isLoading = true)
            } else it
        }

        viewModelScope.launch {
            try {
                // 2. API 호출 (단일 쿼리 전송)
                chatbotRepository.searchChatbot(query = text)
                    .onSuccess { response: ChatbotSearchResponse ->
                        // 3. 성공: 응답 메시지 처리
                        val responseText = response.message

                        // 챗봇 메시지 생성 (UiChatMessage 사용)
                        val botMessage = UiChatMessage(text = responseText, isFromUser = false)

                        _uiState.update { current ->
                            if (current is ChatbotUiState.Success) {
                                current.copy(messages = current.messages + botMessage, isLoading = false)
                            } else current
                        }
                    }
                    .onFailure { exception ->
                        // 4. 실패: 에러 메시지 처리
                        val errorMessage = "오류가 발생했습니다: ${exception.message}"
                        val errorBotMessage = UiChatMessage(text = errorMessage, isFromUser = false)

                        _uiState.update { current ->
                            if (current is ChatbotUiState.Success) {
                                current.copy(messages = current.messages + errorBotMessage, isLoading = false)
                            } else {
                                // 비상 복구 (상태가 꼬였을 때)
                                ChatbotUiState.Success(
                                    messages = currentMessages + userMessage + errorBotMessage,
                                    isLoading = false
                                )
                            }
                        }
                    }
            } catch (e: Exception) {
                _uiState.value = ChatbotUiState.Error(e.message ?: "알 수 없는 에러가 발생했습니다.")
            }
        }
    }
}