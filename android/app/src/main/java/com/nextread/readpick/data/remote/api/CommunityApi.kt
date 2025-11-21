package com.nextread.readpick.data.remote.api

import com.nextread.readpick.data.model.common.ApiResponse
import com.nextread.readpick.data.model.community.*
import retrofit2.Response
import retrofit2.http.*

interface CommunityApi {

    /**
     * 카테고리 목록 조회
     */
    @GET("v1/api/communities/categories")
    suspend fun getCategories(): ApiResponse<List<CommunityCategoryDto>>

    /**
     * 게시물 목록 조회 (페이지네이션)
     */
    @GET("v1/api/communities/posts")
    suspend fun getPosts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("categoryId") categoryId: Long? = null,
        @Query("sort") sort: String = "latest"  // "latest" or "popular"
    ): ApiResponse<CommunityPostPageResponse>

    /**
     * 게시물 상세 조회
     */
    @GET("v1/api/communities/posts/{postId}")
    suspend fun getPostDetail(
        @Path("postId") postId: Long
    ): ApiResponse<CommunityPostDto>

    /**
     * 게시물 작성
     */
    @POST("v1/api/communities/posts")
    suspend fun createPost(
        @Body request: CreatePostRequest
    ): ApiResponse<CommunityPostDto>

    /**
     * 게시물 삭제
     */
    @DELETE("v1/api/communities/posts/{postId}")
    suspend fun deletePost(
        @Path("postId") postId: Long
    ): Response<Unit>

    /**
     * 좋아요 추가
     */
    @POST("v1/api/communities/posts/{postId}/like")
    suspend fun likePost(
        @Path("postId") postId: Long
    ): Response<Unit>

    /**
     * 좋아요 취소
     */
    @DELETE("v1/api/communities/posts/{postId}/like")
    suspend fun unlikePost(
        @Path("postId") postId: Long
    ): Response<Unit>

    /**
     * 댓글 목록 조회
     */
    @GET("v1/api/communities/posts/{postId}/comments")
    suspend fun getComments(
        @Path("postId") postId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50
    ): ApiResponse<CommentPageResponse>

    /**
     * 댓글 작성
     */
    @POST("v1/api/communities/posts/{postId}/comments")
    suspend fun createComment(
        @Path("postId") postId: Long,
        @Body request: CreateCommentRequest
    ): ApiResponse<CommunityCommentDto>

    /**
     * 댓글 삭제
     */
    @DELETE("v1/api/communities/comments/{commentId}")
    suspend fun deleteComment(
        @Path("commentId") commentId: Long
    ): Response<Unit>

    /**
     * 사용자가 작성한 게시물 목록 (읽은 책 수 포함)
     */
    @GET("v1/api/communities/users/{userId}/posts")
    suspend fun getUserPosts(
        @Path("userId") userId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ApiResponse<UserPostsResponse>
}

@kotlinx.serialization.Serializable
data class CommunityPostPageResponse(
    val content: List<CommunityPostDto> = emptyList(),
    val page: PageInfo = PageInfo()
) {
    // 편의 프로퍼티
    val totalElements: Long get() = page.totalElements
    val totalPages: Int get() = page.totalPages
    val size: Int get() = page.size
    val number: Int get() = page.number
    val first: Boolean get() = page.number == 0
    val last: Boolean get() = page.number >= page.totalPages - 1
    val empty: Boolean get() = content.isEmpty()
}

@kotlinx.serialization.Serializable
data class PageInfo(
    val size: Int = 20,
    val number: Int = 0,
    val totalElements: Long = 0,
    val totalPages: Int = 0
)

@kotlinx.serialization.Serializable
data class UserPostsResponse(
    val posts: CommunityPostPageResponse,
    val readBooksCount: Int = 0
)
