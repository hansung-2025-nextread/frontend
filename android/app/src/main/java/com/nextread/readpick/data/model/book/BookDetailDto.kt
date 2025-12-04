package com.nextread.readpick.data.model.book

import kotlinx.serialization.Serializable

@Serializable
data class BookDetailDto(
    val isbn13: String,
    val title: String,
    val author: String,
    val pubDate: String,
    val publisher: String,
    val description: String,
    val cover: String,
    val priceSales: Int,
    val priceStandard: Int,
    val customerReviewRank: Double,
    val ratingScore: Double,
    val ratingCount: Int,
    val link: String,
    val categoryIdList: List<Int> = emptyList()
)
