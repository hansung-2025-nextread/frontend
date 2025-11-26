package com.nextread.readpick.data.model.community

import kotlinx.serialization.Serializable

@Serializable
data class CreatePostRequest(
    val categoryId: Long,
    val postType: String,           // "REVIEW", "QUESTION", "DISCUSSION"
    val title: String,
    val content: String,
    val bookIsbn13: String? = null
)
