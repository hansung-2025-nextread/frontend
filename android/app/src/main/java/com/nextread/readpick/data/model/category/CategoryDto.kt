package com.nextread.readpick.data.model.category

import kotlinx.serialization.Serializable

/**
 * 카테고리 DTO
 */
@Serializable
data class CategoryDto(
    val id: Long,
    val name: String
)
