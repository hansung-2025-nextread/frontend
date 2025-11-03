package com.nextread.readpick.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextread.readpick.domain.repository.AuthRepository
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
 *
 * @param authRepository 인증 Repository (Hilt가 자동 주입)
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // UI 상태 (private mutable, public immutable)
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Google ID Token으로 로그인
     *
     * 호출 순서:
     * 1. Loading 상태로 변경
     * 2. AuthRepository.loginWithGoogle() 호출
     * 3. 성공: Success 상태 → 홈 화면으로 이동
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

            // 2. 로그인 API 호출
            authRepository.loginWithGoogle(idToken)
                .onSuccess {
                    // 3-1. 성공: JWT 토큰 저장 완료
                    android.util.Log.d("LoginViewModel", "✅ Login SUCCESS")
                    _uiState.value = LoginUiState.Success
                }
                .onFailure { exception ->
                    // 3-2. 실패: 에러 메시지
                    android.util.Log.e("LoginViewModel", "❌ Login FAILED: ${exception.message}", exception)
                    _uiState.value = LoginUiState.Error(
                        message = exception.message ?: "로그인에 실패했습니다"
                    )
                }
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
