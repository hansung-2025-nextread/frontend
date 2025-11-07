package com.nextread.readpick.data.repository

import android.util.Log
import com.nextread.readpick.data.model.onboarding.OnboardingCategoryDto
import com.nextread.readpick.data.model.onboarding.SelectCategoriesRequest
import com.nextread.readpick.data.remote.api.OnboardingApi
import com.nextread.readpick.domain.repository.OnboardingRepository
import javax.inject.Inject

/**
 * OnboardingRepository 구현체
 *
 * OnboardingApi를 사용하여 백엔드와 통신하고,
 * Domain Layer의 인터페이스를 구현합니다.
 *
 * @param onboardingApi Hilt로 주입받는 Retrofit API 서비스
 */
class OnboardingRepositoryImpl @Inject constructor(
    private val onboardingApi: OnboardingApi
) : OnboardingRepository {

    override suspend fun checkOnboardingStatus(): Result<Boolean> = runCatching {
        Log.d(TAG, "온보딩 상태 확인 API 호출")

        val response = onboardingApi.getOnboardingStatus()

        if (response.success && response.data != null) {
            val isComplete = response.data.isOnboardingComplete
            Log.d(TAG, "온보딩 완료 여부: $isComplete")
            isComplete
        } else {
            Log.e(TAG, "온보딩 상태 확인 실패: ${response.message}")
            throw Exception(response.message ?: "온보딩 상태를 확인할 수 없습니다")
        }
    }.onFailure { exception ->
        Log.e(TAG, "온보딩 상태 확인 에러", exception)
    }

    override suspend fun getCategories(): Result<List<OnboardingCategoryDto>> = runCatching {
        Log.d(TAG, "온보딩 카테고리 목록 조회 API 호출")

        val response = onboardingApi.getOnboardingCategories()

        if (response.success && response.data != null) {
            Log.d(TAG, "카테고리 ${response.data.size}개 조회 성공")
            response.data
        } else {
            Log.e(TAG, "카테고리 조회 실패: ${response.message}")
            throw Exception(response.message ?: "카테고리 목록을 불러올 수 없습니다")
        }
    }.onFailure { exception ->
        Log.e(TAG, "카테고리 조회 에러", exception)
    }

    override suspend fun submitSelectedCategories(categoryIds: List<Int>): Result<Unit> = runCatching {
        Log.d(TAG, "카테고리 선택 제출 API 호출: $categoryIds")

        val request = SelectCategoriesRequest(categoryIds)
        val response = onboardingApi.selectCategories(request)

        if (response.success) {
            Log.d(TAG, "카테고리 선택 제출 성공")
            Unit  // 명시적으로 Unit 반환
        } else {
            Log.e(TAG, "카테고리 선택 제출 실패: ${response.message}")
            throw Exception(response.message ?: "카테고리 선택을 저장할 수 없습니다")
        }
    }.onFailure { exception ->
        Log.e(TAG, "카테고리 선택 제출 에러", exception)
    }

    companion object {
        private const val TAG = "OnboardingRepository"
    }
}
