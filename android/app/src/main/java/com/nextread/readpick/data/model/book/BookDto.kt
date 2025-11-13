package com.nextread.readpick.data.model.book

import kotlinx.serialization.Serializable

@Serializable
data class BookDto(
    val isbn13: String,
    val title: String,
    val author: String,
    val cover: String,        // 표지 이미지 URL
    val description: String,
    val categoryName: String? = null
)