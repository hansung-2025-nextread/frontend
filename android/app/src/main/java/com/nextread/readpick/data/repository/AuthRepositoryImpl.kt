package com.nextread.readpick.data.repository

import com.nextread.readpick.data.local.TokenManager
import com.nextread.readpick.data.model.auth.LoginRequest
import com.nextread.readpick.data.model.auth.LoginResponse
import com.nextread.readpick.data.model.user.UserInfoDto // 🚨 새로 추가
import com.nextread.readpick.data.model.common.ApiResponse
import com.nextread.readpick.data.remote.api.AuthApi
import com.nextread.readpick.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 인증 Repository 구현체
 *
 * AuthApi와 TokenManager를 사용하여 인증 처리
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) : AuthRepository {

    /**
     * Google 로그인
     *
     * 1. Google ID Token을 백엔드로 전송
     * 2. 백엔드에서 JWT 토큰 받기
     * 3. JWT 토큰과 사용자 정보를 TokenManager에 저장
     *
     * @param idToken Google ID Token
     * @return Result<Unit> 성공/실패
     */
    override suspend fun loginWithGoogle(idToken: String): Result<Unit> {
        return try {
            android.util.Log.d("AuthRepository", "📡 Calling backend API /v1/api/auth/google")

            // 1. 백엔드 API 호출
            val request = LoginRequest(idToken = idToken)
            android.util.Log.d("AuthRepository", "Request: idToken=${idToken.take(50)}...")

            val apiResponse = authApi.login("google", request)
            android.util.Log.d("AuthRepository", "API Response: success=${apiResponse.success}, data=${apiResponse.data}")

            // 2. 응답 검증
            if (!apiResponse.success || apiResponse.data == null) {
                val errorMsg = apiResponse.message ?: "Unknown error"
                android.util.Log.e("AuthRepository", "❌ API failed: $errorMsg")
                return Result.failure(Exception(errorMsg))
            }

            val loginData = apiResponse.data
            android.util.Log.d("AuthRepository", "✅ Login data: accessToken=${loginData.accessToken.take(20)}..., email=${loginData.email}")

            // 3. JWT 토큰 저장
            tokenManager.saveToken(loginData.accessToken)
            android.util.Log.d("AuthRepository", "💾 JWT token saved")

            // 4. 사용자 정보 저장
            tokenManager.saveUserInfo(
                userId = loginData.userId,
                email = loginData.email,
                name = loginData.name,
                picture = loginData.picture
            )
            android.util.Log.d("AuthRepository", "💾 User info saved: id=${loginData.userId}, ${loginData.name}, ${loginData.email}")

            // 5. 사용자 역할 저장
            tokenManager.saveUserRole(loginData.role)
            android.util.Log.d("AuthRepository", "💾 User role saved: ${loginData.role}")

            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "❌ Login failed", e)
            Result.failure(e)
        }
    }

    /**
     * 로그아웃: 서버 요청 후 로컬 토큰 삭제
     */
    override suspend fun logout(): Result<Unit> {
        return try {
            // 1. 서버 로그아웃 요청
            authApi.logout()
            // 2. 로컬 토큰 삭제
            tokenManager.clear()
            Result.success(Unit)
        } catch (e: Exception) {
            tokenManager.clear() // 서버 통신 실패해도 로컬에서 삭제
            Result.failure(e)
        }
    }

    /**
     * 마이페이지: 서버로부터 사용자 프로필 정보를 조회합니다.
     */
    override suspend fun fetchUserProfile(): UserInfoDto {
        val response = authApi.fetchUserProfile()

        if (response.success && response.data != null) {
            return response.data
        } else {
            // 🚨🚨🚨 오류 발생 부분 수정: response.error 참조를 제거하고 일반 메시지를 사용합니다. 🚨🚨🚨
            // 백엔드에서 에러 메시지를 응답 본문에 직접 포함하는 경우를 대비
            val errorMessage = "프로필 조회 실패"

            // 💡 만약 response.message 필드가 있다면:
            // val errorMessage = response.message ?: "프로필 조회 실패"

            throw Exception(errorMessage)

        }
    }

    // 🚨 마이페이지: TokenManager에서 사용자 정보를 가져오는 함수 추가
    override fun getUserInfo(): UserInfoDto? {
        val email = tokenManager.getEmail()
        val name = tokenManager.getName()
        val picture = tokenManager.getPicture()

        return if (email != null && name != null) {
            UserInfoDto(name = name, email = email, profileImageUrl = picture)
        } else {
            null
        }
    }
}
