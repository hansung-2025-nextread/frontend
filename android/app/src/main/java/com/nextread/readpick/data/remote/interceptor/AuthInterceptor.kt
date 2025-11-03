package com.nextread.readpick.data.remote.interceptor

import com.nextread.readpick.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * 인증 인터셉터
 *
 * 모든 API 요청에 JWT 토큰을 자동으로 추가
 *
 * 동작 방식:
 * 1. API 호출 전 intercept() 실행
 * 2. TokenManager에서 토큰 조회
 * 3. Authorization 헤더에 "Bearer {토큰}" 추가
 * 4. 실제 API 호출
 *
 * 예시:
 * ```
 * Before:
 * GET /api/books/123
 * Headers: (없음)
 *
 * After (Interceptor 적용):
 * GET /api/books/123
 * Headers: Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
 * ```
 *
 * 장점:
 * - API 호출할 때마다 토큰을 수동으로 추가할 필요 없음
 * - 토큰 관리가 중앙화됨
 * - 코드 중복 제거
 */
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // 원본 요청 가져오기
        val originalRequest = chain.request()

        // 토큰 조회
        val token = tokenManager.getToken()

        // 토큰이 없으면 원본 요청 그대로 진행 (로그인 API 등)
        if (token.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }

        // 새 요청 생성 (Authorization 헤더 추가)
        val newRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        // 새 요청으로 API 호출
        return chain.proceed(newRequest)
    }
}
