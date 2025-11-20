package com.nextread.readpick.presentation.auth.login

/**
 * 로그인 화면 UI 상태
 *
 * sealed interface를 사용하여 타입 안전한 상태 관리
 *
 * 사용 예시:
 * ```kotlin
 * when (uiState) {
 *     is LoginUiState.Idle -> { /* 초기 상태 */ }
 *     is LoginUiState.Loading -> { /* 로딩 표시 */ }
 *     is LoginUiState.Success -> { /* 로그인 성공 */ }
 *     is LoginUiState.Error -> { /* 에러 메시지 표시 */ }
 * }
 * ```
 */
sealed interface LoginUiState {

    /**
     * 초기 상태
     *
     * 아무 작업도 하지 않은 상태
     */
    data object Idle : LoginUiState

    /**
     * 로딩 중
     *
     * Google 로그인 처리 중
     * UI: 로딩 인디케이터 표시
     */
    data object Loading : LoginUiState

    /**
     * 로그인 성공
     *
     * JWT 토큰이 저장되고 로그인 완료
     * UI: role과 온보딩 필요 여부에 따라 적절한 화면으로 이동
     *
     * @param needsOnboarding 온보딩 필요 여부
     *        - true: 온보딩 화면으로 이동
     *        - false: 홈 화면으로 이동
     * @param isAdmin 관리자 여부
     *        - true: 관리자 대시보드로 이동
     *        - false: 일반 사용자 플로우
     */
    data class Success(
        val needsOnboarding: Boolean,
        val isAdmin: Boolean = false
    ) : LoginUiState

    /**
     * 로그인 실패
     *
     * @param message 에러 메시지
     *
     * UI: 에러 메시지 표시
     */
    data class Error(val message: String) : LoginUiState
}
