package com.nextread.readpick.domain.model

data class Review(
    val id: Long,
    val userName: String,
    val userPicture: String,
    val content: String,
    val createdAt: String,
    val updatedAt: String,
    val isbn13: String?,
    val bookTitle: String?,
    val bookCover: String?
)
