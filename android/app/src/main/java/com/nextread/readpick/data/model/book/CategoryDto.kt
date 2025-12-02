package com.nextread.readpick.data.model.book

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: Long,
    val name: String,
    val weight: Double
)
