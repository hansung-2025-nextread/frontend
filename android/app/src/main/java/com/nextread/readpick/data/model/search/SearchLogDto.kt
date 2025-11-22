package com.nextread.readpick.data.model.search

import kotlinx.serialization.Serializable

@Serializable
data class SearchLogDto(
    val id: Long,
    val query: String,
    val createdAt: String
)
