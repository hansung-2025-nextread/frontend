package com.nextread.readpick.presentation.mypage

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
import com.nextread.readpick.domain.usecase.user.DeleteSearchHistoryUseCase
import com.nextread.readpick.domain.usecase.user.GetUserInfoUseCase


sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Failure(val exception: Exception) : Result<Nothing>()
}
@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val deleteSearchHistoryUseCase: DeleteSearchHistoryUseCase, // 🚨 쉼표(,) 추가
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {

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
    }

    fun onLogoutClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = logoutUseCase.execute()) {
                is Result.Success<*> -> {
                    _uiState.update { it.copy(isLoading = false, isLoggedOut = true) }
                }
                is Result.Failure -> {
                    _uiState.update { it.copy(isLoading = false, error = result.exception.message) }
                }
            }
        }
    }

    // 🚨🚨🚨 [네비게이션 해결용] 로그아웃 상태를 초기화하는 함수 🚨🚨🚨
    // Screen에서 LaunchedEffect 내부에 onNavigateToLogin() 호출 후 호출됩니다.
    fun resetLogoutState() {
        _uiState.update { it.copy(isLoggedOut = false) }
    }

    fun onDeleteSearchHistory() {
        viewModelScope.launch {
            deleteSearchHistoryUseCase.execute()
            // 성공 후 메시지 표시 등의 로직 구현
        }
    }
}