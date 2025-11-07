package com.nextread.readpick.domain.repository

import com.nextread.readpick.data.model.onboarding.OnboardingCategoryDto

/**
 * 온보딩 Repository 인터페이스
 *
 * Clean Architecture의 Domain Layer에 위치하며,
 * Data Layer의 구현체(OnboardingRepositoryImpl)로부터 의존성이 역전됩니다.
 *
 * 온보딩 관련 비즈니스 로직을 제공합니다.
 */
interface OnboardingRepository {

    /**
     * 온보딩 완료 여부 확인
     *
     * 로그인 직후 호출하여 사용자가 온보딩을 완료했는지 확인합니다.
     *
     * @return Result<Boolean>
     *         - Success(true): 온보딩 완료 → Home으로 이동
     *         - Success(false): 온보딩 필요 → Onboarding으로 이동
     *         - Failure: 네트워크 오류 또는 서버 에러
     */
    suspend fun checkOnboardingStatus(): Result<Boolean>

    /**
     * 온보딩 카테고리 목록 조회
     *
     * 온보딩 화면에 표시할 8개 카테고리를 가져옵니다.
     *
     * @return Result<List<OnboardingCategoryDto>>
     *         - Success: 카테고리 목록 (8개)
     *         - Failure: 네트워크 오류 또는 서버 에러
     */
    suspend fun getCategories(): Result<List<OnboardingCategoryDto>>

    /**
     * 선택한 카테고리 제출
     *
     * 사용자가 선택한 카테고리를 서버에 전송하고 온보딩을 완료합니다.
     * 빈 리스트 전송 시 "건너뛰기"로 처리됩니다.
     *
     * @param categoryIds 선택한 카테고리 ID 목록 (빈 리스트 가능)
     * @return Result<Unit>
     *         - Success: 온보딩 완료 성공
     *         - Failure: 네트워크 오류 또는 서버 에러
     */
    suspend fun submitSelectedCategories(categoryIds: List<Int>): Result<Unit>
}
