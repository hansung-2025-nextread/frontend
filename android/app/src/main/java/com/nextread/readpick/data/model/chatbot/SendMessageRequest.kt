package com.nextread.readpick.data.model.chatbot

import kotlinx.serialization.Serializable

@Serializable
data class SendMessageRequest(
    val message: String
)
