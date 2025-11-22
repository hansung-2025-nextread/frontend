package com.nextread.readpick.data.model.community

import kotlinx.serialization.Serializable

@Serializable
data class CommunityCommentDto(
    val id: Long,
    val content: String,
    val createdAt: String,
    // 작성자 정보
    val authorId: Long,
    val authorName: String,
    val authorPicture: String? = null
)

@Serializable
data class CommentPageResponse(
    val content: List<CommunityCommentDto> = emptyList(),
    val page: PageInfo = PageInfo()
)

@Serializable
data class PageInfo(
    val size: Int = 50,
    val number: Int = 0,
    val totalElements: Long = 0,
    val totalPages: Int = 0
)
