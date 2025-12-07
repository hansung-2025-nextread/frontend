package com.nextread.readpick.domain.model

enum class ReadingStatus(val displayName: String, val emoji: String) {
    NOT_STARTED("읽기 전", "📖"),
    READING("읽는 중", "📚"),
    COMPLETED("완독", "✅"),
    DROPPED("중단", "❌");

    companion object {
        fun fromString(value: String): ReadingStatus {
            return values().find { it.name == value } ?: NOT_STARTED
        }
    }
}
