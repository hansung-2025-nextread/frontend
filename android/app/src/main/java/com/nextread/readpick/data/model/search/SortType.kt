package com.nextread.readpick.data.model.search

enum class SortType(val value: String, val displayName: String) {
    ACCURACY("ACCURACY", "관련도"),
    LATEST("LATEST", "최신순"),
    SALES("SALES", "판매량순"),
    PRICE_LOW("PRICE_LOW", "낮은 가격순"),
    PRICE_HIGH("PRICE_HIGH", "높은 가격순")
}
