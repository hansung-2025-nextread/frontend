package com.nextread.readpick.domain.model

data class Book(
    val isbn13: String,
    val title: String,
    val author: String,
    val cover: String,
    val description: String,
    val categoryName: String?
)
