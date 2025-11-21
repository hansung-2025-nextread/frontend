package com.nextread.readpick.data.model.community

import kotlinx.serialization.Serializable

@Serializable
data class CommunityCategoryDto(
    val id: Long,
    val name: String
)
