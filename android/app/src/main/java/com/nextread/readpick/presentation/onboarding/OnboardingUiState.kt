package com.nextread.readpick.presentation.onboarding

import com.nextread.readpick.data.model.onboarding.OnboardingCategoryDto

/**
 * 온보딩 화면의 UI 상태를 나타내는 sealed class
 *
 * MVVM 패턴에서 ViewModel이 UI 상태를 관리하고,
 * Composable은 이 상태를 구독하여 UI를 렌더링합니다.
 */
sealed class OnboardingUiState {

    /**
     * 초기 로딩 상태
     * - 카테고리 목록을 서버에서 가져오는 중
     */
    data object Loading : OnboardingUiState()

    /**
     * 성공 상태
     * - 카테고리 목록 로딩 완료
     * - 사용자가 카테고리 선택 가능
     *
     * @param categories 온보딩 카테고리 목록 (8개)
     * @param selectedCategoryIds 사용자가 선택한 카테고리 ID 목록
     * @param isSubmitting 카테고리 선택을 서버에 전송 중인지 여부
     */
    data class Success(
        val categories: List<OnboardingCategoryDto>,
        val selectedCategoryIds: Set<Int> = emptySet(),
        val isSubmitting: Boolean = false
    ) : OnboardingUiState()

    /**
     * 에러 상태
     * - 카테고리 목록 조회 실패
     * - 네트워크 오류 또는 서버 에러
     *
     * @param message 에러 메시지
     */
    data class Error(
        val message: String
    ) : OnboardingUiState()
}
