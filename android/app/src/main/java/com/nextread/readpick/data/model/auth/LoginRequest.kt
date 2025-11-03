package com.nextread.readpick.data.model.auth

import kotlinx.serialization.Serializable

/**
 * 로그인 요청 DTO
 *
 * 백엔드 API: POST /auth/{provider}
 *
 * @param idToken Google에서 받은 ID Token
 *
 * 사용 예시:
 * ```kotlin
 * val request = LoginRequest(idToken = "eyJhbGciOiJSUzI1NiIs...")
 * val response = authApi.login("google", request)
 * ```
 */
@Serializable
data class LoginRequest(
    val idToken: String
)
