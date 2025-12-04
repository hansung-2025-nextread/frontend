package com.nextread.readpick.data.model.search

import kotlinx.serialization.Serializable

@Serializable
data class SearchPageResponse(
    val books: List<SearchBookDto> = emptyList(),
    val page: PageInfo = PageInfo()
)

@Serializable
data class PageInfo(
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val size: Int = 20,
    val number: Int = 0,
    val first: Boolean = true,
    val last: Boolean = true
)
