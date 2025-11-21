package com.nextread.readpick.data.model.search

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponseData(
    val books: List<SearchBookDto>
)