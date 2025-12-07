package com.nextread.readpick.data.remote.api

import com.nextread.readpick.data.model.common.ApiResponse
import com.nextread.readpick.data.model.review.ReportReviewRequest
import com.nextread.readpick.data.model.review.ReviewDto
import com.nextread.readpick.data.model.review.ReviewPageResponse
import com.nextread.readpick.data.model.review.ReviewRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 리뷰 API 인터페이스
 * 참고: 백엔드에서 ApiResponse로 감싸지 않고 데이터를 직접 반환
 */
interface ReviewApi {
    /**
     * 특정 책의 리뷰 목록 조회 (페이지네이션)
     */
    @GET("v1/api/books/{isbn13}/reviews")
    suspend fun getReviews(
        @Path("isbn13") isbn13: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ReviewPageResponse

    /**
     * 리뷰 작성
     * @param isbn13 책 ISBN
     * @param request 리뷰 내용
     * @return 생성된 리뷰
     */
    @POST("v1/api/books/{isbn13}/reviews")
    suspend fun createReview(
        @Path("isbn13") isbn13: String,
        @Body request: ReviewRequest
    ): ReviewDto

    /**
     * 내 리뷰 수정
     * @param isbn13 책 ISBN
     * @param request 수정할 리뷰 내용
     * @return 수정된 리뷰
     */
    @PUT("v1/api/books/{isbn13}/reviews")
    suspend fun updateReview(
        @Path("isbn13") isbn13: String,
        @Body request: ReviewRequest
    ): ReviewDto

    /**
     * 내 리뷰 삭제
     * @param isbn13 책 ISBN
     * 참고: 백엔드가 204 No Content 반환
     */
    @DELETE("v1/api/books/{isbn13}/reviews")
    suspend fun deleteReview(
        @Path("isbn13") isbn13: String
    ): Response<Unit>

    /**
     * 리뷰 신고
     * @param reviewId 신고할 리뷰 ID
     * @param request 신고 사유
     * 참고: plain text 응답이므로 ResponseBody 사용
     */
    @POST("v1/api/reviews/{reviewId}/report")
    suspend fun reportReview(
        @Path("reviewId") reviewId: Long,
        @Body request: ReportReviewRequest
    ): ResponseBody
}
