package com.nextread.readpick.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextread.readpick.data.local.TokenManager
import com.nextread.readpick.domain.repository.AuthRepository
import com.nextread.readpick.domain.repository.OnboardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 로그인 화면 ViewModel
 *
 * Google 로그인 처리 및 UI 상태 관리
 * 로그인 성공 후 온보딩 필요 여부를 확인하여 적절한 화면으로 이동
 *
 * @param authRepository 인증 Repository (Hilt가 자동 주입)
 * @param onboardingRepository 온보딩 Repository (Hilt가 자동 주입)
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val onboardingRepository: OnboardingRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    // UI 상태 (private mutable, public immutable)
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Google ID Token으로 로그인
     *
     * 호출 순서:
     * 1. Loading 상태로 변경
     * 2. AuthRepository.loginWithGoogle() 호출 (JWT 토큰 저장)
     * 3. OnboardingRepository.checkOnboardingStatus() 호출 (온보딩 여부 확인)
     * 4. 성공: Success(needsOnboarding) 상태 → Onboarding 또는 Home 화면으로 이동
     *    실패: Error 상태 → 에러 메시지 표시
     *
     * @param idToken Google에서 받은 ID Token
     *
     * 사용 예시 (LoginScreen에서):
     * ```kotlin
     * val credential = ...  // Google Credential
     * val idToken = credential.googleIdToken
     * viewModel.loginWithGoogle(idToken)
     * ```
     */
    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            android.util.Log.d("LoginViewModel", "🔑 loginWithGoogle called with token: ${idToken.take(50)}...")

            // 1. 로딩 상태
            _uiState.value = LoginUiState.Loading
            android.util.Log.d("LoginViewModel", "State changed to: Loading")

            // 2. 로그인 API 호출 (JWT 토큰 저장)
            authRepository.loginWithGoogle(idToken)
                .onSuccess {
                    android.util.Log.d("LoginViewModel", "✅ Login SUCCESS - 토큰 저장 완료")

                    // 3. 온보딩 상태 확인
                    android.util.Log.d("LoginViewModel", "📋 온보딩 상태 확인 중...")
                    checkOnboardingStatusAndNavigate()
                }
                .onFailure { exception ->
                    // 실패: 에러 메시지
                    android.util.Log.e("LoginViewModel", "❌ Login FAILED: ${exception.message}", exception)
                    _uiState.value = LoginUiState.Error(
                        message = exception.message ?: "로그인에 실패했습니다"
                    )
                }
        }
    }

    /**
     * 온보딩 상태 확인 및 화면 전환 결정
     *
     * 로그인 성공 후 호출되어 사용자가 온보딩을 완료했는지 확인합니다.
     * - 관리자: Success(isAdmin = true) → AdminDashboard로 이동
     * - 온보딩 완료: Success(needsOnboarding = false) → Home으로 이동
     * - 온보딩 필요: Success(needsOnboarding = true) → Onboarding으로 이동
     */
    private suspend fun checkOnboardingStatusAndNavigate() {
        // 관리자인 경우 바로 AdminDashboard로 이동
        val isAdmin = tokenManager.isAdmin()
        if (isAdmin) {
            android.util.Log.d("LoginViewModel", "👑 관리자 계정 - AdminDashboard로 이동")
            _uiState.value = LoginUiState.Success(
                needsOnboarding = false,
                isAdmin = true
            )
            return
        }

        // 일반 사용자는 온보딩 상태 확인
        onboardingRepository.checkOnboardingStatus()
            .onSuccess { isOnboardingComplete ->
                val needsOnboarding = !isOnboardingComplete
                android.util.Log.d(
                    "LoginViewModel",
                    "온보딩 완료 여부: $isOnboardingComplete (needsOnboarding: $needsOnboarding)"
                )

                _uiState.value = LoginUiState.Success(
                    needsOnboarding = needsOnboarding,
                    isAdmin = false
                )

                if (needsOnboarding) {
                    android.util.Log.d("LoginViewModel", "➡️ Onboarding 화면으로 이동")
                } else {
                    android.util.Log.d("LoginViewModel", "➡️ Home 화면으로 이동")
                }
            }
            .onFailure { exception ->
                android.util.Log.e("LoginViewModel", "❌ 온보딩 상태 확인 실패: ${exception.message}", exception)
                // 온보딩 상태 확인 실패 시 기본적으로 온보딩 화면으로 이동
                // (안전장치: 신규 사용자일 가능성을 고려)
                _uiState.value = LoginUiState.Success(
                    needsOnboarding = true,
                    isAdmin = false
                )
                android.util.Log.d("LoginViewModel", "⚠️ 온보딩 상태 확인 실패 - 기본값으로 Onboarding 화면 이동")
            }
    }

    /**
     * 에러 상태 초기화
     *
     * 에러 메시지를 닫을 때 호출
     */
    fun clearError() {
        _uiState.value = LoginUiState.Idle
    }
}
