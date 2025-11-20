package com.nextread.readpick.data.model.chatbot

// ★ Gson 임포트 제거
// import com.google.gson.annotations.SerializedName

// ★ Kotlinx Serialization 임포트 추가
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ★ @Serializable 어노테이션 필수!
@Serializable
data class ChatbotSearchRequest(
    val query: String
)

@Serializable
data class ChatbotSearchResponse(
    @SerialName("message") // @SerializedName 대신 @SerialName 사용
    val message: String,

    @SerialName("books")
    val books: List<BookDto>
)

@Serializable
data class BookDto(
    val title: String,
    val author: String,
    val price: Int,
    val thumbnailUrl: String
)