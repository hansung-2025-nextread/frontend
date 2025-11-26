package com.nextread.readpick.data.model.book

import kotlinx.serialization.Serializable

@Serializable
data class SavedBookDto(
    val isbn13: String,
    val title: String,
    val author: String,
    val cover: String,
    val readingStatus: String = "NOT_STARTED",  // NOT_STARTED, READING, COMPLETED, DROPPED
    val startedAt: String? = null,
    val completedAt: String? = null
)

@Serializable
data class SavedBookPageResponse(
    val content: List<SavedBookDto> = emptyList(),
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val size: Int = 20,
    val number: Int = 0,
    val first: Boolean = true,
    val last: Boolean = true,
    val empty: Boolean = true
)
