package com.nextread.readpick.data.model.collection

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 컬렉션 내 책 응답
 *
 * 백엔드 API: GET /api/users/me/collections/{collectionId}/books
 */
@Serializable
data class CollectionBookResponse(
    val isbn13: String,
    val title: String,
    val author: String,
    val cover: String,
    val publisher: String,
    @SerialName("readingStatus") val readingStatus: String? = null,
    @SerialName("startedAt") val startedAt: String? = null,
    @SerialName("completedAt") val completedAt: String? = null
)
