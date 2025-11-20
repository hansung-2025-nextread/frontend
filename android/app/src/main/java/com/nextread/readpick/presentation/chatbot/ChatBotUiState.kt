package com.nextread.readpick.presentation.chatbot

/**
 * 챗봇 메시지를 나타내는 데이터 클래스
 * @param text 메시지 내용
 * @param isFromUser 사용자가 보낸 메시지인지 여부
 */
data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(), // 메시지 구분을 위한 고유 ID
    val text: String,
    val isFromUser: Boolean
)

/**
 * 챗봇 화면의 UI 상태를 나타내는 sealed interface
 * 가이드라인의 HomeUiState 패턴을 따릅니다.
 */
sealed interface ChatbotUiState {
    /** 로딩 중 상태 */
    data object Loading : ChatbotUiState

    /** 데이터 로드 성공 상태 */
    data class Success(
        val messages: List<ChatMessage> // 채팅 메시지 목록
    ) : ChatbotUiState

    /** 에러 발생 상태 */
    data class Error(val message: String) : ChatbotUiState
}