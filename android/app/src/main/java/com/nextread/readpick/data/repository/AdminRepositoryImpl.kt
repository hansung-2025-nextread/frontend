package com.nextread.readpick.data.repository

import android.util.Log
import com.nextread.readpick.data.model.admin.HideReviewRequest
import com.nextread.readpick.data.model.admin.ReportedReviewDto
import com.nextread.readpick.data.model.admin.ReviewDetailDto
import com.nextread.readpick.data.model.admin.SuspendUserRequest
import com.nextread.readpick.data.model.admin.UserDetailDto
import com.nextread.readpick.data.model.admin.UserReviewDto
import com.nextread.readpick.data.remote.api.AdminApi
import com.nextread.readpick.domain.repository.AdminRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 관리자 Repository 구현체
 */
@Singleton
class AdminRepositoryImpl @Inject constructor(
    private val adminApi: AdminApi
) : AdminRepository {

    companion object {
        private const val TAG = "AdminRepository"
    }

    override suspend fun getReportedReviews(): Result<List<ReportedReviewDto>> = runCatching {
        Log.d(TAG, "📡 Fetching reported reviews")
        val response = adminApi.getReportedReviews()
        if (response.success && response.data != null) {
            Log.d(TAG, "✅ Fetched ${response.data.content.size} reported reviews")
            response.data.content
        } else {
            throw Exception(response.message ?: "신고된 리뷰를 불러오는데 실패했습니다")
        }
    }.onFailure { e ->
        Log.e(TAG, "❌ Failed to fetch reported reviews", e)
    }

    override suspend fun getUserReviews(userId: Long): Result<List<UserReviewDto>> = runCatching {
        Log.d(TAG, "📡 Fetching reviews for user $userId")
        val response = adminApi.getUserReviews(userId)
        if (response.success && response.data != null) {
            Log.d(TAG, "✅ Fetched ${response.data.content.size} reviews for user $userId")
            response.data.content
        } else {
            throw Exception(response.message ?: "사용자 리뷰를 불러오는데 실패했습니다")
        }
    }.onFailure { e ->
        Log.e(TAG, "❌ Failed to fetch user reviews", e)
    }

    override suspend fun getReviewDetail(reviewId: Long): Result<ReviewDetailDto> = runCatching {
        Log.d(TAG, "📡 Fetching review detail for $reviewId")
        val response = adminApi.getReviewDetail(reviewId)
        if (response.success && response.data != null) {
            Log.d(TAG, "✅ Fetched review detail for $reviewId")
            response.data
        } else {
            throw Exception(response.message ?: "리뷰 상세를 불러오는데 실패했습니다")
        }
    }.onFailure { e ->
        Log.e(TAG, "❌ Failed to fetch review detail", e)
    }

    override suspend fun hideReview(reviewId: Long, reason: String): Result<Unit> = runCatching {
        Log.d(TAG, "📡 Hiding review $reviewId")
        val response = adminApi.hideReview(reviewId, HideReviewRequest(reason))
        if (response.success) {
            Log.d(TAG, "✅ Review $reviewId hidden successfully")
            Unit
        } else {
            throw Exception(response.message ?: "리뷰 숨김에 실패했습니다")
        }
    }.onFailure { e ->
        Log.e(TAG, "❌ Failed to hide review", e)
    }

    override suspend fun getUserDetail(userId: Long): Result<UserDetailDto> = runCatching {
        Log.d(TAG, "📡 Fetching user detail for $userId")
        val response = adminApi.getUserDetail(userId)
        if (response.success && response.data != null) {
            Log.d(TAG, "✅ Fetched user detail for $userId")
            response.data
        } else {
            throw Exception(response.message ?: "사용자 정보를 불러오는데 실패했습니다")
        }
    }.onFailure { e ->
        Log.e(TAG, "❌ Failed to fetch user detail", e)
    }

    override suspend fun suspendUser(userId: Long, reason: String): Result<Unit> = runCatching {
        Log.d(TAG, "📡 Suspending user $userId")
        val response = adminApi.suspendUser(userId, SuspendUserRequest(reason))
        if (response.success) {
            Log.d(TAG, "✅ User $userId suspended successfully")
            Unit
        } else {
            throw Exception(response.message ?: "사용자 정지에 실패했습니다")
        }
    }.onFailure { e ->
        Log.e(TAG, "❌ Failed to suspend user", e)
    }

    override suspend fun unsuspendUser(userId: Long): Result<Unit> = runCatching {
        Log.d(TAG, "📡 Unsuspending user $userId")
        val response = adminApi.unsuspendUser(userId)
        if (response.success) {
            Log.d(TAG, "✅ User $userId unsuspended successfully")
            Unit
        } else {
            throw Exception(response.message ?: "사용자 정지 해제에 실패했습니다")
        }
    }.onFailure { e ->
        Log.e(TAG, "❌ Failed to unsuspend user", e)
    }
}
