package com.nextread.readpick.domain.repository

import com.nextread.readpick.data.model.admin.ReportedReviewDto
import com.nextread.readpick.data.model.admin.ReviewDetailDto
import com.nextread.readpick.data.model.admin.UserDetailDto
import com.nextread.readpick.data.model.admin.UserReviewDto

/**
 * 관리자 Repository 인터페이스
 */
interface AdminRepository {

    /**
     * 신고된 리뷰 목록 조회
     */
    suspend fun getReportedReviews(): Result<List<ReportedReviewDto>>

    /**
     * 사용자별 리뷰 목록 조회
     */
    suspend fun getUserReviews(userId: Long): Result<List<UserReviewDto>>

    /**
     * 리뷰 상세 조회
     */
    suspend fun getReviewDetail(reviewId: Long): Result<ReviewDetailDto>

    /**
     * 리뷰 숨김 처리
     */
    suspend fun hideReview(reviewId: Long, reason: String): Result<Unit>

    /**
     * 사용자 상세 조회
     */
    suspend fun getUserDetail(userId: Long): Result<UserDetailDto>

    /**
     * 사용자 정지
     */
    suspend fun suspendUser(userId: Long, reason: String): Result<Unit>

    /**
     * 사용자 정지 해제
     */
    suspend fun unsuspendUser(userId: Long): Result<Unit>
}
