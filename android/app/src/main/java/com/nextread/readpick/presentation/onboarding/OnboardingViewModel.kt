package com.nextread.readpick.presentation.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextread.readpick.domain.repository.OnboardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 온보딩 화면의 ViewModel
 *
 * 역할:
 * - 카테고리 목록 조회
 * - 사용자 선택 상태 관리
 * - 카테고리 선택 제출 (완료 또는 건너뛰기)
 *
 * @param onboardingRepository Hilt로 주입받는 OnboardingRepository
 */
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) : ViewModel() {

    // UI 상태 (StateFlow)
    private val _uiState = MutableStateFlow<OnboardingUiState>(OnboardingUiState.Loading)
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    // Navigation 이벤트 (SharedFlow - 일회성 이벤트)
    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        // ViewModel 생성 시 카테고리 목록 자동 로딩
        loadCategories()
    }

    /**
     * 온보딩 카테고리 목록 로딩
     *
     * API: GET /v1/api/onboarding/categories
     * 8개의 카테고리를 서버에서 가져옵니다.
     */
    fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = OnboardingUiState.Loading
            Log.d(TAG, "카테고리 목록 로딩 시작")

            onboardingRepository.getCategories()
                .onSuccess { categories ->
                    Log.d(TAG, "카테고리 ${categories.size}개 로딩 성공")
                    _uiState.value = OnboardingUiState.Success(
                        categories = categories,
                        selectedCategoryIds = emptySet(),
                        isSubmitting = false
                    )
                }
                .onFailure { exception ->
                    Log.e(TAG, "카테고리 로딩 실패", exception)
                    _uiState.value = OnboardingUiState.Error(
                        message = exception.message ?: "카테고리를 불러올 수 없습니다"
                    )
                }
        }
    }

    /**
     * 카테고리 선택/해제 토글
     *
     * 사용자가 카테고리 카드를 클릭하면 선택 상태가 토글됩니다.
     * - 이미 선택된 카테고리: 선택 해제
     * - 선택되지 않은 카테고리: 선택
     *
     * @param categoryId 선택/해제할 카테고리 ID
     */
    fun toggleCategorySelection(categoryId: Int) {
        val currentState = _uiState.value
        if (currentState !is OnboardingUiState.Success) return

        val updatedSelection = if (categoryId in currentState.selectedCategoryIds) {
            // 이미 선택된 경우 → 선택 해제
            currentState.selectedCategoryIds - categoryId
        } else {
            // 선택되지 않은 경우 → 선택 추가
            currentState.selectedCategoryIds + categoryId
        }

        _uiState.update {
            currentState.copy(selectedCategoryIds = updatedSelection)
        }

        Log.d(TAG, "카테고리 선택 변경: $updatedSelection")
    }

    /**
     * 선택한 카테고리 제출 (완료 버튼)
     *
     * API: POST /v1/api/onboarding/select-categories
     * 사용자가 선택한 카테고리를 서버에 전송하고 온보딩을 완료합니다.
     */
    fun submitSelectedCategories() {
        val currentState = _uiState.value
        if (currentState !is OnboardingUiState.Success) return

        viewModelScope.launch {
            // 제출 중 상태로 변경 (로딩 표시)
            _uiState.update {
                currentState.copy(isSubmitting = true)
            }

            val selectedIds = currentState.selectedCategoryIds.toList()
            Log.d(TAG, "선택한 카테고리 제출: $selectedIds")

            onboardingRepository.submitSelectedCategories(selectedIds)
                .onSuccess {
                    Log.d(TAG, "카테고리 제출 성공")
                    // 홈 화면으로 이동 이벤트 발행
                    _navigationEvent.emit(NavigationEvent.NavigateToHome)
                }
                .onFailure { exception ->
                    Log.e(TAG, "카테고리 제출 실패", exception)
                    // 제출 중 상태 해제
                    _uiState.update {
                        currentState.copy(isSubmitting = false)
                    }
                    // 에러 상태로 전환
                    _uiState.value = OnboardingUiState.Error(
                        message = exception.message ?: "카테고리 선택을 저장할 수 없습니다"
                    )
                }
        }
    }

    /**
     * 온보딩 건너뛰기
     *
     * API: POST /v1/api/onboarding/select-categories (빈 배열)
     * 카테고리를 선택하지 않고 온보딩을 완료합니다.
     */
    fun skipOnboarding() {
        val currentState = _uiState.value
        if (currentState !is OnboardingUiState.Success) return

        viewModelScope.launch {
            _uiState.update {
                currentState.copy(isSubmitting = true)
            }

            Log.d(TAG, "온보딩 건너뛰기")

            onboardingRepository.submitSelectedCategories(emptyList())
                .onSuccess {
                    Log.d(TAG, "온보딩 건너뛰기 성공")
                    _navigationEvent.emit(NavigationEvent.NavigateToHome)
                }
                .onFailure { exception ->
                    Log.e(TAG, "온보딩 건너뛰기 실패", exception)
                    _uiState.update {
                        currentState.copy(isSubmitting = false)
                    }
                    _uiState.value = OnboardingUiState.Error(
                        message = exception.message ?: "온보딩을 건너뛸 수 없습니다"
                    )
                }
        }
    }

    /**
     * Navigation 이벤트
     * SharedFlow를 사용하여 일회성 이벤트를 발행합니다.
     */
    sealed class NavigationEvent {
        data object NavigateToHome : NavigationEvent()
    }

    companion object {
        private const val TAG = "OnboardingViewModel"
    }
}
