package com.nextread.readpick.data.model.chatbot

import kotlinx.serialization.Serializable

//메세지 전송
@Serializable
data class MessageRequest(
    val message: String
)

//응답 데이터
@Serializable
data class ChatResponseDto(
    val sessionId: Long,  // 또는 Long (명세서엔 0으로 되어있지만 String이 안전함)
    val reply: String,      // AI의 답변
    val books: List<ChatBookDto> = emptyList() // 🚨 중요: 추천 도서 목록 추가됨!
)

//응답 내 도서 정보
@Serializable
data class ChatBookDto(
    val title: String,
    val isbn13: String,
    val author: String,
    val cover: String,
    val link: String? = null,
    val description: String? = null
    // 필요한 필드만 적어도 됩니다. (price, publisher 등은 선택사항)
)

//세션 목록 Dto
@Serializable
data class ChatSessionDto(
    val sessionId: Long,
    val title: String,
    val lastMessage: String?,
    val createdAt: String
)

//앱 내부 UI 그리기용 Dto
data class ChatMessageDto(
    val messageId: String,  // 리스트에서 구분하기 위한 ID
    val sender: String,     // "USER" (나) 또는 "AI" (챗봇)
    val content: String,    // 메시지 내용 (reply 또는 message)
    val timestamp: String,  // 보낸 시간
    val books: List<ChatBookDto> = emptyList() // (선택사항) 추천 책이 있다면 여기에 담음
)

@Serializable
data class CreateSessionResponse(val sessionId: Long, val title: String)

