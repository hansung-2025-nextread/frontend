package com.nextread.readpick.data.model.community

import kotlinx.serialization.Serializable

@Serializable
data class CommunityPostDto(
    val id: Long,
    val categoryId: Long,
    val categoryName: String,
    val postType: String,           // "REVIEW", "QUESTION", "DISCUSSION"
    val title: String,
    val content: String,
    val viewCount: Int = 0,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val liked: Boolean = false,
    val createdAt: String,          // ISO 8601 format
    val updatedAt: String? = null,
    // 작성자 정보
    val authorId: Long,
    val authorName: String,
    val authorPicture: String? = null,
    // 연결된 책 정보 (선택)
    val bookIsbn13: String? = null,
    val bookTitle: String? = null,
    val bookCover: String? = null
)

@Serializable
data class CommunityBookDto(
    val isbn13: String,
    val title: String,
    val author: String,
    val cover: String
)
