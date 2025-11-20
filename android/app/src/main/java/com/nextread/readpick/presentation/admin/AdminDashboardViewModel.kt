package com.nextread.readpick.presentation.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextread.readpick.domain.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    companion object {
        private const val TAG = "AdminDashboardViewModel"
    }

    private val _uiState = MutableStateFlow<AdminDashboardUiState>(AdminDashboardUiState.Loading)
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    private val _selectedTab = MutableStateFlow(AdminTab.REPORTED_REVIEWS)
    val selectedTab: StateFlow<AdminTab> = _selectedTab.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    init {
        loadReportedReviews()
    }

    fun selectTab(tab: AdminTab) {
        _selectedTab.value = tab
    }

    fun loadReportedReviews() {
        viewModelScope.launch {
            _uiState.value = AdminDashboardUiState.Loading
            adminRepository.getReportedReviews()
                .onSuccess { reviews ->
                    Log.d(TAG, "✅ Loaded ${reviews.size} reported reviews")
                    _uiState.value = AdminDashboardUiState.Success(
                        reportedReviews = reviews
                    )
                }
                .onFailure { e ->
                    Log.e(TAG, "❌ Failed to load reported reviews", e)
                    _uiState.value = AdminDashboardUiState.Error(
                        e.message ?: "신고된 리뷰를 불러오는데 실패했습니다"
                    )
                }
        }
    }

    fun loadReviewDetail(reviewId: Long) {
        viewModelScope.launch {
            adminRepository.getReviewDetail(reviewId)
                .onSuccess { detail ->
                    Log.d(TAG, "✅ Loaded review detail for $reviewId")
                    val currentState = _uiState.value
                    if (currentState is AdminDashboardUiState.Success) {
                        _uiState.value = currentState.copy(selectedReview = detail)
                    }
                }
                .onFailure { e ->
                    Log.e(TAG, "❌ Failed to load review detail", e)
                    _toastMessage.emit(e.message ?: "리뷰 상세를 불러오는데 실패했습니다")
                }
        }
    }

    fun hideReview(reviewId: Long, reason: String) {
        viewModelScope.launch {
            adminRepository.hideReview(reviewId, reason)
                .onSuccess {
                    Log.d(TAG, "✅ Review $reviewId hidden successfully")
                    _toastMessage.emit("리뷰가 숨김 처리되었습니다")
                    clearSelectedReview()
                    loadReportedReviews()
                }
                .onFailure { e ->
                    Log.e(TAG, "❌ Failed to hide review", e)
                    _toastMessage.emit(e.message ?: "리뷰 숨김에 실패했습니다")
                }
        }
    }

    fun loadUserDetail(userId: Long) {
        viewModelScope.launch {
            // 사용자 정보 로드
            adminRepository.getUserDetail(userId)
                .onSuccess { detail ->
                    Log.d(TAG, "✅ Loaded user detail for $userId")
                    val currentState = _uiState.value
                    if (currentState is AdminDashboardUiState.Success) {
                        _uiState.value = currentState.copy(selectedUser = detail)
                    }
                    // 사용자 리뷰 목록도 로드
                    loadUserReviews(userId)
                }
                .onFailure { e ->
                    Log.e(TAG, "❌ Failed to load user detail", e)
                    _toastMessage.emit(e.message ?: "사용자 정보를 불러오는데 실패했습니다")
                }
        }
    }

    fun loadUserReviews(userId: Long) {
        viewModelScope.launch {
            adminRepository.getUserReviews(userId)
                .onSuccess { reviews ->
                    Log.d(TAG, "✅ Loaded ${reviews.size} reviews for user $userId")
                    val currentState = _uiState.value
                    if (currentState is AdminDashboardUiState.Success) {
                        _uiState.value = currentState.copy(userReviews = reviews)
                    }
                }
                .onFailure { e ->
                    Log.e(TAG, "❌ Failed to load user reviews", e)
                    _toastMessage.emit(e.message ?: "사용자 리뷰를 불러오는데 실패했습니다")
                }
        }
    }

    fun suspendUser(userId: Long, reason: String) {
        viewModelScope.launch {
            adminRepository.suspendUser(userId, reason)
                .onSuccess {
                    Log.d(TAG, "✅ User $userId suspended successfully")
                    _toastMessage.emit("사용자가 정지되었습니다")
                    loadUserDetail(userId)
                }
                .onFailure { e ->
                    Log.e(TAG, "❌ Failed to suspend user", e)
                    _toastMessage.emit(e.message ?: "사용자 정지에 실패했습니다")
                }
        }
    }

    fun unsuspendUser(userId: Long) {
        viewModelScope.launch {
            adminRepository.unsuspendUser(userId)
                .onSuccess {
                    Log.d(TAG, "✅ User $userId unsuspended successfully")
                    _toastMessage.emit("사용자 정지가 해제되었습니다")
                    loadUserDetail(userId)
                }
                .onFailure { e ->
                    Log.e(TAG, "❌ Failed to unsuspend user", e)
                    _toastMessage.emit(e.message ?: "사용자 정지 해제에 실패했습니다")
                }
        }
    }

    fun clearSelectedReview() {
        val currentState = _uiState.value
        if (currentState is AdminDashboardUiState.Success) {
            _uiState.value = currentState.copy(selectedReview = null)
        }
    }

    fun clearSelectedUser() {
        val currentState = _uiState.value
        if (currentState is AdminDashboardUiState.Success) {
            _uiState.value = currentState.copy(selectedUser = null)
        }
    }
}
