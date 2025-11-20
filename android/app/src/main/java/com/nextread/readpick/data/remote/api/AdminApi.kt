package com.nextread.readpick.data.remote.api

import com.nextread.readpick.data.model.admin.HideReviewRequest
import com.nextread.readpick.data.model.admin.ReportedReviewDto
import com.nextread.readpick.data.model.admin.ReviewDetailDto
import com.nextread.readpick.data.model.admin.SuspendUserRequest
import com.nextread.readpick.data.model.admin.UserDetailDto
import com.nextread.readpick.data.model.admin.UserReviewDto
import com.nextread.readpick.data.model.common.ApiResponse
import com.nextread.readpick.data.model.common.PageResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 관리자 API 인터페이스
 */
interface AdminApi {

    /**
     * 신고된 리뷰 목록 조회
     */
    @GET("v1/api/admin/reviews/reported")
    suspend fun getReportedReviews(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ApiResponse<PageResponse<ReportedReviewDto>>

    /**
     * 사용자별 리뷰 목록 조회
     */
    @GET("v1/api/admin/reviews/user/{userId}")
    suspend fun getUserReviews(
        @Path("userId") userId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ApiResponse<PageResponse<UserReviewDto>>

    /**
     * 리뷰 상세 조회
     */
    @GET("v1/api/admin/reviews/{reviewId}")
    suspend fun getReviewDetail(
        @Path("reviewId") reviewId: Long
    ): ApiResponse<ReviewDetailDto>

    /**
     * 리뷰 숨김 처리 (삭제)
     */
    @HTTP(method = "DELETE", path = "v1/api/admin/reviews/{reviewId}", hasBody = true)
    suspend fun hideReview(
        @Path("reviewId") reviewId: Long,
        @Body request: HideReviewRequest
    ): ApiResponse<Unit>

    /**
     * 사용자 상세 조회
     */
    @GET("v1/api/admin/users/{userId}")
    suspend fun getUserDetail(
        @Path("userId") userId: Long
    ): ApiResponse<UserDetailDto>

    /**
     * 사용자 정지
     */
    @POST("v1/api/admin/users/{userId}/suspend")
    suspend fun suspendUser(
        @Path("userId") userId: Long,
        @Body request: SuspendUserRequest
    ): ApiResponse<Unit>

    /**
     * 사용자 정지 해제
     */
    @POST("v1/api/admin/users/{userId}/unsuspend")
    suspend fun unsuspendUser(
        @Path("userId") userId: Long
    ): ApiResponse<Unit>
}
