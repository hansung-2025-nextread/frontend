package com.nextread.readpick.data.model.common

import kotlinx.serialization.Serializable

/**
 * Spring Page 응답을 위한 DTO
 * 모든 필드에 기본값을 제공하여 유연하게 처리
 */
@Serializable
data class PageResponse<T>(
    val content: List<T> = emptyList(),
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val size: Int = 20,
    val number: Int = 0,
    val first: Boolean = true,
    val last: Boolean = true,
    val empty: Boolean = true,
    val numberOfElements: Int = 0
)
