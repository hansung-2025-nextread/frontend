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

// ★ 중요: 서버 전송용 DTO 객체는 'ApiChatMessage'라는 별명으로 임포트하여 UI 객체와 구분합니다.
import com.nextread.readpick.data.model.chatbot.ChatMessage as ApiChatMessage

/**
 * 챗봇 화면의 로직을 담당하는 ViewModel
 */
@HiltViewModel
class ChatbotViewModel @Inject constructor(
    private val chatbotRepository: ChatbotRepository
) : ViewModel() {

    // ★ 초기 상태 설정: 앱을 켜자마자 'Success' 상태로 시작하여 대화창을 바로 보여줍니다.
    // ChatMessage는 같은 패키지(presentation.chatbot)에 있는 UI용 클래스입니다.
    private val _uiState = MutableStateFlow<ChatbotUiState>(
        ChatbotUiState.Success(
            messages = listOf(
                ChatMessage(
                    text = "Next Read에 오신 것을 환영합니다! 원하는 책을 추천받아 보세요.",
                    isFromUser = false
                )
            ),
            isLoading = false
        )
    )
    val uiState: StateFlow<ChatbotUiState> = _uiState.asStateFlow()

    // init 블록은 제거했습니다. (위의 초기값으로 대체됨)

    /**
     * 사용자가 메시지를 입력하고 전송 버튼을 눌렀을 때 호출
     */
    fun sendMessage(text: String) {
        val currentState = _uiState.value

        // 로딩 중이거나 에러 상태일 때는 중복 전송을 막습니다.
        if (currentState !is ChatbotUiState.Success || currentState.isLoading) {
            return
        }

        // 1. 화면에 보여줄 사용자 메시지 생성 (UI용 ChatMessage)
        val userMessage = ChatMessage(text = text, isFromUser = true)
        val currentMessages = currentState.messages

        // 2. UI 즉시 업데이트: 사용자 메시지 추가 + 로딩 표시 시작
        _uiState.update {
            if (it is ChatbotUiState.Success) {
                it.copy(messages = currentMessages + userMessage, isLoading = true)
            } else it
        }

        viewModelScope.launch {
            try {
                // 3. API 전송을 위해 UI 메시지 리스트를 DTO(ApiChatMessage) 리스트로 변환
                // map 함수 내부에서 명시적으로 ApiChatMessage 생성자를 호출합니다.
                val historyForApi: List<ApiChatMessage> = currentMessages.map { msg ->
                    ApiChatMessage(
                        role = if (msg.isFromUser) "user" else "assistant",
                        content = msg.text
                    )
                }

                // 4. Repository를 통해 실제 API 호출
                chatbotRepository.getChatResponse(
                    userMessage = text,
                    history = historyForApi
                )
                    .onSuccess { responseText ->
                        // 5. 성공 시: 챗봇 응답 메시지 생성 (UI용 ChatMessage)
                        val botMessage = ChatMessage(text = responseText, isFromUser = false)

                        // UI 업데이트: 챗봇 메시지 추가 + 로딩 표시 종료
                        _uiState.update { current ->
                            if (current is ChatbotUiState.Success) {
                                current.copy(messages = current.messages + botMessage, isLoading = false)
                            } else current
                        }
                    }
                    .onFailure { exception ->
                        // 6. 실패 시: 에러 메시지를 챗봇 말풍선으로 보여줌
                        val errorMessage = "죄송합니다. 오류가 발생했습니다: ${exception.message}"
                        val errorBotMessage = ChatMessage(text = errorMessage, isFromUser = false)

                        _uiState.update { current ->
                            if (current is ChatbotUiState.Success) {
                                current.copy(messages = current.messages + errorBotMessage, isLoading = false)
                            } else {
                                // 만약 상태가 꼬였을 경우를 대비한 복구 코드
                                ChatbotUiState.Success(
                                    messages = currentMessages + userMessage + errorBotMessage,
                                    isLoading = false
                                )
                            }
                        }
                    }
            } catch (e: Exception) {
                // 예상치 못한 시스템 에러 처리
                _uiState.value = ChatbotUiState.Error(e.message ?: "알 수 없는 에러가 발생했습니다.")
            }
        }
    }
}