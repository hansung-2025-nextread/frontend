package com.nextread.readpick.data.model.book

import kotlinx.serialization.Serializable

@Serializable
data class PersonalizedRecommendationResponse(
    val categories: List<CategoryDto>,
    val books: List<BookDetailDto>
)
