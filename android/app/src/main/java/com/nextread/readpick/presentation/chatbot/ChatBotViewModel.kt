package com.nextread.readpick.presentation.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 챗봇 화면의 ViewModel
 * 가이드라인의 HomeViewModel 및 API 호출 스니펫을 따릅니다.
 */
@HiltViewModel
class ChatbotViewModel @Inject constructor(
    // 추후 챗봇 Repository를 주입받습니다.
    // private val chatbotRepository: ChatbotRepository
) : ViewModel() {

    // UI 상태를 관리하는 MutableStateFlow
    // 초기 상태는 비어있는 메시지 목록을 가진 Success 상태입니다.
    private val _uiState = MutableStateFlow<ChatbotUiState>(ChatbotUiState.Success(emptyList()))
    val uiState: StateFlow<ChatbotUiState> = _uiState.asStateFlow()

    /**
     * 사용자가 메시지를 전송할 때 호출되는 함수
     */
    fun sendMessage(text: String) {
        // 1. 사용자의 메시지를 즉시 UI에 추가
        val userMessage = ChatMessage(text = text, isFromUser = true)

        _uiState.update { currentState ->
            if (currentState is ChatbotUiState.Success) {
                currentState.copy(messages = currentState.messages + userMessage)
            } else {
                // 이전 상태가 Success가 아니었다면(예: Error),
                // 새 메시지 목록으로 Success 상태를 만듭니다.
                ChatbotUiState.Success(listOf(userMessage))
            }
        }

        // 2. 챗봇 응답을 위해 viewModelScope 사용 (가이드 스니펫 참고)
        viewModelScope.launch {
            try {
                // TODO: 추후 여기서 Repository를 호출하여 실제 API 통신
                // 지금은 1초 딜레이 후 가짜 응답을 생성합니다.
                delay(1000)

                val botResponseText = "안녕하세요! \"$text\"라고 말씀하셨네요. 지금은 간단한 답변만 가능해요."
                val botMessage = ChatMessage(text = botResponseText, isFromUser = false)

                // 3. 챗봇의 응답을 UI에 추가
                _uiState.update { currentState ->
                    if (currentState is ChatbotUiState.Success) {
                        currentState.copy(messages = currentState.messages + botMessage)
                    } else {
                        // 이전 상태가 바뀌었다면(예: Error), 현재 상태를 유지
                        currentState
                    }
                }
            } catch (e: Exception) {
                // 4. API 호출 실패 시 Error 상태로 변경 (가이드 스니펫 참고)
                _uiState.value = ChatbotUiState.Error(e.message ?: "알 수 없는 에러가 발생했습니다.")
            }
        }
    }
}