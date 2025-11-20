package com.nextread.readpick.data.model.search

import kotlinx.serialization.Serializable

@Serializable
data class SearchBookDto(
    val title: String,
    val isbn13: String,
    val author: String,
    val pubDate: String?,      // null 가능성 대비
    val publisher: String?,
    val description: String?,
    val cover: String,         // 표지 이미지
    val priceSales: Int?,
    val priceStandard: Int?,
    val customerReviewRank: Double?,
    val ratingScore: Double?,  // 실수형 (float/double)
    val ratingCount: Int?,
    val link: String?,
    val categoryIdList: List<Int>? = emptyList()
)