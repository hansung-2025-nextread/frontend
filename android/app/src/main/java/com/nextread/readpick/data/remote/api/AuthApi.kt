package com.nextread.readpick.data.remote.api

import com.nextread.readpick.data.model.auth.LoginRequest
import com.nextread.readpick.data.model.auth.LoginResponse
import com.nextread.readpick.data.model.common.ApiResponse
import com.nextread.readpick.data.model.user.UserInfoDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * 인증 관련 API 인터페이스
 * 백엔드 인증 엔드포인트를 정의
 */
interface AuthApi {

    @POST("v1/api/auth/{provider}")
    suspend fun login(
        @Path("provider") provider: String,
        @Body request: LoginRequest
    ): ApiResponse<LoginResponse>

    /**
     * 현재 로그인된 사용자의 프로필 정보를 조회합니다.
     */
    @GET("v1/api/user/profile")
    suspend fun fetchUserProfile(): ApiResponse<UserInfoDto> // 🚨 ApiResponse<UserInfoDto> 반환

    /**
     * 사용자 로그아웃을 요청합니다.
     */
    @POST("v1/api/auth/logout")
    suspend fun logout(): ApiResponse<Unit> // 🚨 ApiResponse<Unit> 반환
}