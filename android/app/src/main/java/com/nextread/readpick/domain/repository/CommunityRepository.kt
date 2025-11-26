package com.nextread.readpick.domain.repository

import com.nextread.readpick.data.model.community.*
import com.nextread.readpick.data.remote.api.CommunityPostPageResponse
import com.nextread.readpick.data.remote.api.UserPostsResponse

interface CommunityRepository {

    /**
     * 카테고리 목록 조회
     */
    suspend fun getCategories(): Result<List<CommunityCategoryDto>>

    /**
     * 게시물 목록 조회
     */
    suspend fun getPosts(
        page: Int = 0,
        size: Int = 20,
        categoryId: Long? = null,
        sort: String = "createdAt,desc"
    ): Result<CommunityPostPageResponse>

    /**
     * 게시물 상세 조회
     */
    suspend fun getPostDetail(postId: Long): Result<CommunityPostDto>

    /**
     * 게시물 작성
     */
    suspend fun createPost(request: CreatePostRequest): Result<CommunityPostDto>

    /**
     * 게시물 삭제
     */
    suspend fun deletePost(postId: Long): Result<Unit>

    /**
     * 좋아요 추가
     */
    suspend fun likePost(postId: Long): Result<Unit>

    /**
     * 좋아요 취소
     */
    suspend fun unlikePost(postId: Long): Result<Unit>

    /**
     * 댓글 목록 조회
     */
    suspend fun getComments(postId: Long, page: Int = 0, size: Int = 50): Result<List<CommunityCommentDto>>

    /**
     * 댓글 작성
     */
    suspend fun createComment(postId: Long, content: String): Result<CommunityCommentDto>

    /**
     * 댓글 삭제
     */
    suspend fun deleteComment(postId: Long, commentId: Long): Result<Unit>

    /**
     * 사용자가 작성한 게시물 목록 (읽은 책 수 포함)
     */
    suspend fun getUserPosts(userId: Long, page: Int = 0, size: Int = 20): Result<UserPostsResponse>
}
