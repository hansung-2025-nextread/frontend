package com.nextread.readpick.data.model.common

import kotlinx.serialization.Serializable

/**
 * 공통 API 응답 래퍼
 *
 * 백엔드가 일관된 응답 형식을 사용하는 경우에만 사용
 * (현재 ReadPick 백엔드는 직접 데이터를 반환하므로 필요시에만 사용)
 *
 * 예시:
 * ```json
 * {
 *   "success": true,
 *   "data": { ... },
 *   "message": "Success"
 * }
 * ```
 */
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
)
