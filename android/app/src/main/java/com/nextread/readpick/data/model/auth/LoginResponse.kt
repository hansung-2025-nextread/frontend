package com.nextread.readpick.data.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 로그인 응답 데이터
 *
 * 백엔드 API: POST /v1/api/auth/{provider}
 *
 * @param accessToken JWT 토큰 (백엔드가 accessToken으로 반환)
 * @param email 사용자 이메일
 * @param name 사용자 이름
 * @param picture 프로필 사진 URL
 *
 * 응답 예시:
 * ```json
 * {
 *   "success": true,
 *   "data": {
 *     "accessToken": "eyJ...",
 *     "email": "user@gmail.com",
 *     "name": "박지호",
 *     "picture": "https://...",
 *     "role": "USER"
 *   }
 * }
 * ```
 */
@Serializable
data class LoginResponse(
    val userId: Long,
    @SerialName("accessToken") val accessToken: String,  // 백엔드는 accessToken으로 반환
    val email: String,
    val name: String,
    val picture: String,
    val role: String = "USER"  // USER 또는 ADMIN
)
