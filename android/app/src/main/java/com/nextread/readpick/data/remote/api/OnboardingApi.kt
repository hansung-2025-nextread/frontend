package com.nextread.readpick.data.remote.api

import com.nextread.readpick.data.model.common.ApiResponse
import com.nextread.readpick.data.model.onboarding.OnboardingCategoryDto
import com.nextread.readpick.data.model.onboarding.OnboardingStatusResponse
import com.nextread.readpick.data.model.onboarding.SelectCategoriesRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * 온보딩 관련 API 인터페이스
 *
 * Base URL: BuildConfig.BASE_URL (local.properties에서 읽어옴)
 * 인증: AuthInterceptor가 자동으로 JWT 토큰을 Authorization 헤더에 추가
 *
 * 관련 백엔드 가이드: onboarding_guide.md
 */
interface OnboardingApi {

    /**
     * 온보딩 완료 여부 확인
     *
     * API: GET /v1/api/users/me/onboarding-status
     * 인증: 필수 (JWT Bearer Token)
     *
     * 로그인 직후 호출하여 온보딩 화면 표시 여부를 판단합니다.
     *
     * @return ApiResponse<OnboardingStatusResponse>
     *         - isOnboardingComplete = true: 홈으로 이동
     *         - isOnboardingComplete = false: 온보딩 화면으로 이동
     */
    @GET("v1/api/users/me/onboarding-status")
    suspend fun getOnboardingStatus(): ApiResponse<OnboardingStatusResponse>

    /**
     * 온보딩 카테고리 목록 조회
     *
     * API: GET /v1/api/onboarding/categories
     * 인증: 불필요 (누구나 조회 가능)
     *
     * 온보딩 화면에 표시할 8개 카테고리를 가져옵니다.
     * - 50917: 한국소설
     * - 55889: 에세이
     * - 336: 자기계발
     * - 170: 경제경영
     * - 51395: 심리학/정신분석학
     * - 169: 세계사 일반
     * - 987: 과학
     * - 1196: 여행
     *
     * @return ApiResponse<List<OnboardingCategoryDto>>
     */
    @GET("v1/api/onboarding/categories")
    suspend fun getOnboardingCategories(): ApiResponse<List<OnboardingCategoryDto>>

    /**
     * 카테고리 선택 완료
     *
     * API: POST /v1/api/onboarding/select-categories
     * 인증: 필수 (JWT Bearer Token)
     *
     * 사용자가 선택한 카테고리들을 서버에 전송합니다.
     * - 각 카테고리는 가중치 1.0으로 사용자 선호도에 저장됩니다.
     * - 빈 배열 전송 시 "건너뛰기"로 처리됩니다.
     *
     * @param request SelectCategoriesRequest
     *                - categoryIds: 선택한 카테고리 ID 목록 (빈 배열 가능)
     * @return ApiResponse<Unit>
     */
    @POST("v1/api/onboarding/select-categories")
    suspend fun selectCategories(
        @Body request: SelectCategoriesRequest
    ): ApiResponse<Unit>
}
