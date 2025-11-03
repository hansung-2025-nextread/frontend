package com.nextread.readpick.data.remote.api

import com.nextread.readpick.data.model.auth.LoginRequest
import com.nextread.readpick.data.model.auth.LoginResponse
import com.nextread.readpick.data.model.common.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * 인증 관련 API 인터페이스
 *
 * 백엔드 인증 엔드포인트를 정의
 */
interface AuthApi {

    /**
     * OAuth 로그인
     *
     * @param provider OAuth 제공자 ("google", "kakao", "naver")
     * @param request 로그인 요청 (idToken 포함)
     * @return API 응답 래퍼 (success, data, error)
     *
     * API: POST /v1/api/auth/{provider}
     *
     * 사용 예시:
     * ```kotlin
     * val request = LoginRequest(idToken = googleIdToken)
     * val response = authApi.login("google", request)
     * // response.data?.accessToken을 TokenManager에 저장
     * ```
     */
    @POST("v1/api/auth/{provider}")
    suspend fun login(
        @Path("provider") provider: String,
        @Body request: LoginRequest
    ): ApiResponse<LoginResponse>
}
