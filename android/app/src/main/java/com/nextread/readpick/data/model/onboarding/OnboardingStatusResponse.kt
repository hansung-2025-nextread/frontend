package com.nextread.readpick.data.model.onboarding

import kotlinx.serialization.Serializable

/**
 * 온보딩 완료 여부 응답 DTO
 *
 * API: GET /v1/api/users/me/onboarding-status
 * 로그인 직후 호출하여 사용자가 온보딩을 완료했는지 확인합니다.
 */
@Serializable
data class OnboardingStatusResponse(
    /**
     * 온보딩 완료 여부
     * - true: 온보딩 이미 완료 → Home 화면으로 이동
     * - false: 온보딩 필요 → Onboarding 화면으로 이동
     */
    val isOnboardingComplete: Boolean
)
