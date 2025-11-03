package com.nextread.readpick.data.model.common

import kotlinx.serialization.Serializable

/**
 * 공통 에러 응답
 *
 * 백엔드 에러 응답 형식 (Spring Boot 기본 형식)
 *
 * 예시:
 * ```json
 * {
 *   "timestamp": "2025-10-30T12:00:00",
 *   "status": 404,
 *   "error": "Not Found",
 *   "message": "Book not found",
 *   "path": "/api/books/123"
 * }
 * ```
 *
 * 사용 예시:
 * ```kotlin
 * try {
 *     val book = bookApi.getBookDetail(isbn13)
 * } catch (e: HttpException) {
 *     val errorBody = e.response()?.errorBody()?.string()
 *     val error = Json.decodeFromString<ErrorResponse>(errorBody!!)
 *     println("Error: ${error.message}")
 * }
 * ```
 */
@Serializable
data class ErrorResponse(
    val timestamp: String,
    val status: Int,
    val error: String,
    val message: String,
    val path: String
)
