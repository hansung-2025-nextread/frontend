package com.nextread.readpick.domain.usecase.review

import com.nextread.readpick.domain.model.Review
import com.nextread.readpick.domain.repository.ReviewRepository
import javax.inject.Inject

class GetUserReviewsUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository
) {
    suspend operator fun invoke(page: Int, size: Int): Result<List<Review>> {
        return reviewRepository.getUserReviews(page, size).map { pageResponse ->
            pageResponse.content.map { dto ->
                Review(
                    id = dto.id,
                    userName = dto.userName,
                    userPicture = dto.userPicture,
                    content = dto.content,
                    createdAt = dto.createdAt,
                    updatedAt = dto.updatedAt,
                    isbn13 = dto.isbn13,
                    bookTitle = dto.bookTitle,
                    bookCover = dto.bookCover
                )
            }
        }
    }
}
