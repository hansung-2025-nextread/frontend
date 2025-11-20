package com.nextread.readpick.data.model.search

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponseData(
    val type: String, // 예: "SEARCH"
    val matchedCategory: MatchedCategory?,
    val books: List<SearchBookDto>,
    val needsCategorySelection: Boolean,
    val categoryOptions: List<CategoryOption>?
)

@Serializable
data class MatchedCategory(
    val id: Int,
    val name: String
)

@Serializable
data class CategoryOption(
    val id: Int,
    val name: String,
    val similarity: Double
)