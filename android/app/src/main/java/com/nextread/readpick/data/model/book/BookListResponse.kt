package com.nextread.readpick.data.model.book

import kotlinx.serialization.Serializable

@Serializable
data class BookListResponse(
    val books: List<BookDto>,
    val totalCount: Int
)