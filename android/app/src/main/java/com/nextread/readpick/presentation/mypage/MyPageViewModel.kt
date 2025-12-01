package com.nextread.readpick.presentation.mypage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextread.readpick.data.model.user.UserInfoDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.nextread.readpick.domain.usecase.auth.LogoutUseCase
import com.nextread.readpick.domain.usecase.user.GetUserInfoUseCase

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "MyPageViewModel"
    }

    data class MyPageState(
        val userInfo: UserInfoDto? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val isLoggedOut: Boolean = false
    )

    private val _uiState = MutableStateFlow(MyPageState())
    val uiState: StateFlow<MyPageState> = _uiState

    init {
        loadUserInfo()
    }

    private fun loadUserInfo() {
        val user = getUserInfoUseCase()
        _uiState.update { it.copy(userInfo = user) }
        Log.d(TAG, "사용자 정보 로드: ${user?.name}")
    }

    /**
     * 로그아웃 버튼 클릭 시 호출
     *
     * LogoutUseCase를 통해 DataStore의 JWT 토큰과 사용자 정보를 삭제합니다.
     */
    fun onLogoutClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            Log.d(TAG, "로그아웃 시작...")

            try {
                // LogoutUseCase 실행 (TokenManager.clear() 호출)
                logoutUseCase.execute()

                Log.d(TAG, "✅ 로그아웃 성공")
                _uiState.update { it.copy(isLoading = false, isLoggedOut = true) }
            } catch (e: Exception) {
                Log.e(TAG, "❌ 로그아웃 실패", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "로그아웃에 실패했습니다"
                    )
                }
            }
        }
    }

    /**
     * 로그아웃 상태를 초기화하는 함수
     *
     * Screen에서 LaunchedEffect 내부에서 onNavigateToLogin() 호출 후 호출됩니다.
     * 이를 통해 중복 네비게이션 및 무한 루프를 방지합니다.
     */
    fun resetLogoutState() {
        _uiState.update { it.copy(isLoggedOut = false) }
        Log.d(TAG, "로그아웃 상태 초기화 완료")
    }
}