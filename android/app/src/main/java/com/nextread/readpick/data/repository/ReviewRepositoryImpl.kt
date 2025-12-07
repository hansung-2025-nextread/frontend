package com.nextread.readpick.data.repository

import android.util.Log
import com.nextread.readpick.data.model.review.ReportReviewRequest
import com.nextread.readpick.data.model.review.ReviewDto
import com.nextread.readpick.data.model.review.ReviewPageResponse
import com.nextread.readpick.data.model.review.ReviewRequest
import com.nextread.readpick.data.remote.api.ReviewApi
import com.nextread.readpick.domain.repository.ReviewRepository
import javax.inject.Inject

/**
 * 리뷰 Repository 구현체
 * 참고: 백엔드에서 ApiResponse 없이 데이터를 직접 반환
 */
class ReviewRepositoryImpl @Inject constructor(
    private val reviewApi: ReviewApi
) : ReviewRepository {

    override suspend fun getReviews(
        isbn13: String,
        page: Int
    ): Result<ReviewPageResponse> = runCatching {
        Log.d(TAG, "리뷰 조회 API 호출: isbn13=$isbn13, page=$page")

        val pageResponse = reviewApi.getReviews(isbn13, page, 20)
        Log.d(TAG, "리뷰 조회 성공: ${pageResponse.content.size}개")

        pageResponse
    }.onFailure { exception ->
        Log.e(TAG, "리뷰 조회 에러: isbn13=$isbn13", exception)
    }

    override suspend fun createReview(
        isbn13: String,
        content: String
    ): Result<ReviewDto> = runCatching {
        Log.d(TAG, "리뷰 작성 API 호출: isbn13=$isbn13")

        val request = ReviewRequest(content)
        val review = reviewApi.createReview(isbn13, request)
        Log.d(TAG, "리뷰 작성 성공: reviewId=${review.id}")

        review
    }.onFailure { exception ->
        Log.e(TAG, "리뷰 작성 에러: isbn13=$isbn13", exception)
    }

    override suspend fun updateReview(
        isbn13: String,
        content: String
    ): Result<ReviewDto> = runCatching {
        Log.d(TAG, "리뷰 수정 API 호출: isbn13=$isbn13")

        val request = ReviewRequest(content)
        val review = reviewApi.updateReview(isbn13, request)
        Log.d(TAG, "리뷰 수정 성공: reviewId=${review.id}")

        review
    }.onFailure { exception ->
        Log.e(TAG, "리뷰 수정 에러: isbn13=$isbn13", exception)
    }

    override suspend fun deleteReview(isbn13: String): Result<Unit> = runCatching {
        Log.d(TAG, "리뷰 삭제 API 호출: isbn13=$isbn13")

        val response = reviewApi.deleteReview(isbn13)
        if (response.isSuccessful) {
            Log.d(TAG, "리뷰 삭제 성공: HTTP ${response.code()}")
            Unit
        } else {
            throw Exception("리뷰 삭제 실패: HTTP ${response.code()}")
        }
    }.onFailure { exception ->
        Log.e(TAG, "리뷰 삭제 에러: isbn13=$isbn13", exception)
    }

    override suspend fun reportReview(
        reviewId: Long,
        reason: String
    ): Result<Unit> = runCatching {
        Log.d(TAG, "리뷰 신고 API 호출: reviewId=$reviewId, reason=$reason")

        val request = ReportReviewRequest(reason)
        val response = reviewApi.reportReview(reviewId, request)
        val message = response.string()
        Log.d(TAG, "리뷰 신고 성공: $message")
        Unit
    }.onFailure { exception ->
        Log.e(TAG, "리뷰 신고 에러: reviewId=$reviewId", exception)
    }

    companion object {
        private const val TAG = "ReviewRepository"
    }
}
