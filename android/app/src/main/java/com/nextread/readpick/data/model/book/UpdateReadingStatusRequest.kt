package com.nextread.readpick.data.model.book

import kotlinx.serialization.Serializable

@Serializable
data class UpdateReadingStatusRequest(
    val status: String  // "NOT_STARTED", "READING", "COMPLETED", "DROPPED"
)
