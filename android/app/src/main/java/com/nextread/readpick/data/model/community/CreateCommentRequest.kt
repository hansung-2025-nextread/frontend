package com.nextread.readpick.data.model.community

import kotlinx.serialization.Serializable

@Serializable
data class CreateCommentRequest(
    val content: String
)
