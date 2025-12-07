package com.nextread.readpick.domain.repository

import com.nextread.readpick.data.model.review.ReviewDto
import com.nextread.readpick.data.model.review.ReviewPageResponse

/**
 * 리뷰 Repository 인터페이스
 */
interface ReviewRepository {
    /**
     * 특정 책의 리뷰 목록 조회
     * @param isbn13 책 ISBN
     * @param page 페이지 번호
     * @return 리뷰 페이지 응답
     */
    suspend fun getReviews(
        isbn13: String,
        page: Int = 0
    ): Result<ReviewPageResponse>

    /**
     * 리뷰 작성
     * @param isbn13 책 ISBN
     * @param content 리뷰 내용
     * @return 생성된 리뷰
     */
    suspend fun createReview(
        isbn13: String,
        content: String
    ): Result<ReviewDto>

    /**
     * 내 리뷰 수정
     * @param isbn13 책 ISBN
     * @param content 수정할 리뷰 내용
     * @return 수정된 리뷰
     */
    suspend fun updateReview(
        isbn13: String,
        content: String
    ): Result<ReviewDto>

    /**
     * 내 리뷰 삭제
     * @param isbn13 책 ISBN
     */
    suspend fun deleteReview(isbn13: String): Result<Unit>

    /**
     * 리뷰 신고
     * @param reviewId 신고할 리뷰 ID
     * @param reason 신고 사유
     */
    suspend fun reportReview(
        reviewId: Long,
        reason: String
    ): Result<Unit>
}
