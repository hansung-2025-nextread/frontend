package com.nextread.readpick.data.model.collection

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class UserCollectionResponse(
    val id: Long,
    val name: String,
    @SerialName("bookCount") val bookCount: Long,
    @SerialName("createdAt") val createdAt: String
)