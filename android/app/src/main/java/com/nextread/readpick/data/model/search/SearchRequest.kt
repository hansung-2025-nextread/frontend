package com.nextread.readpick.data.model.search

import kotlinx.serialization.Serializable

@Serializable
data class SearchRequest(
    val query: String,
    val sortBy: String? = null,  // "ACCURACY", "LATEST", "SALES", "PRICE_LOW", "PRICE_HIGH"
    val page: Int = 0,
    val size: Int = 20
)