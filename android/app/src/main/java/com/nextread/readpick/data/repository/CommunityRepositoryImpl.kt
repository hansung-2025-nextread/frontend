package com.nextread.readpick.data.repository

import android.util.Log
import com.nextread.readpick.data.model.community.*
import com.nextread.readpick.data.remote.api.CommunityApi
import com.nextread.readpick.data.remote.api.CommunityPostPageResponse
import com.nextread.readpick.data.remote.api.UserPostsResponse
import com.nextread.readpick.domain.repository.CommunityRepository
import javax.inject.Inject

class CommunityRepositoryImpl @Inject constructor(
    private val communityApi: CommunityApi
) : CommunityRepository {

    override suspend fun getCategories(): Result<List<CommunityCategoryDto>> = runCatching {
        val response = communityApi.getCategories()
        if (response.success && response.data != null) {
            response.data
        } else {
            throw Exception(response.message ?: "카테고리를 불러올 수 없습니다")
        }
    }.onFailure { exception ->
        Log.e(TAG, "카테고리 조회 에러", exception)
    }

    override suspend fun getPosts(
        page: Int,
        size: Int,
        categoryId: Long?,
        sort: String
    ): Result<CommunityPostPageResponse> = runCatching {
        val response = communityApi.getPosts(page, size, categoryId, sort)
        if (response.success && response.data != null) {
            response.data
        } else {
            throw Exception(response.message ?: "게시물을 불러올 수 없습니다")
        }
    }.onFailure { exception ->
        Log.e(TAG, "게시물 목록 조회 에러", exception)
    }

    override suspend fun getPostDetail(postId: Long): Result<CommunityPostDto> = runCatching {
        val response = communityApi.getPostDetail(postId)
        if (response.success && response.data != null) {
            response.data
        } else {
            throw Exception(response.message ?: "게시물을 불러올 수 없습니다")
        }
    }.onFailure { exception ->
        Log.e(TAG, "게시물 상세 조회 에러", exception)
    }

    override suspend fun createPost(request: CreatePostRequest): Result<CommunityPostDto> = runCatching {
        Log.d(TAG, "게시물 작성: ${request.title}")
        val response = communityApi.createPost(request)
        if (response.success && response.data != null) {
            response.data
        } else {
            throw Exception(response.message ?: "게시물을 작성할 수 없습니다")
        }
    }.onFailure { exception ->
        Log.e(TAG, "게시물 작성 에러", exception)
    }

    override suspend fun deletePost(postId: Long): Result<Unit> = runCatching {
        val response = communityApi.deletePost(postId)
        if (response.isSuccessful) {
            Unit
        } else {
            throw Exception("게시물을 삭제할 수 없습니다: ${response.code()}")
        }
    }.onFailure { exception ->
        Log.e(TAG, "게시물 삭제 에러", exception)
    }

    override suspend fun likePost(postId: Long): Result<Unit> = runCatching {
        val response = communityApi.likePost(postId)
        if (response.isSuccessful) {
            Unit
        } else {
            throw Exception("좋아요를 추가할 수 없습니다: ${response.code()}")
        }
    }.onFailure { exception ->
        Log.e(TAG, "좋아요 추가 에러", exception)
    }

    override suspend fun unlikePost(postId: Long): Result<Unit> = runCatching {
        val response = communityApi.unlikePost(postId)
        if (response.isSuccessful) {
            Unit
        } else {
            throw Exception("좋아요를 취소할 수 없습니다: ${response.code()}")
        }
    }.onFailure { exception ->
        Log.e(TAG, "좋아요 취소 에러", exception)
    }

    override suspend fun getComments(postId: Long, page: Int, size: Int): Result<List<CommunityCommentDto>> = runCatching {
        val response = communityApi.getComments(postId, page, size)
        if (response.success && response.data != null) {
            response.data.content
        } else {
            throw Exception(response.message ?: "댓글을 불러올 수 없습니다")
        }
    }.onFailure { exception ->
        Log.e(TAG, "댓글 목록 조회 에러", exception)
    }

    override suspend fun createComment(postId: Long, content: String): Result<CommunityCommentDto> = runCatching {
        val request = CreateCommentRequest(content)
        val response = communityApi.createComment(postId, request)
        if (response.success && response.data != null) {
            response.data
        } else {
            throw Exception(response.message ?: "댓글을 작성할 수 없습니다")
        }
    }.onFailure { exception ->
        Log.e(TAG, "댓글 작성 에러", exception)
    }

    override suspend fun deleteComment(postId: Long, commentId: Long): Result<Unit> = runCatching {
        val response = communityApi.deleteComment(commentId)
        if (response.isSuccessful) {
            Unit
        } else {
            throw Exception("댓글을 삭제할 수 없습니다: ${response.code()}")
        }
    }.onFailure { exception ->
        Log.e(TAG, "댓글 삭제 에러", exception)
    }

    override suspend fun getUserPosts(userId: Long, page: Int, size: Int): Result<UserPostsResponse> = runCatching {
        val response = communityApi.getUserPosts(userId, page, size)
        if (response.success && response.data != null) {
            response.data
        } else {
            throw Exception(response.message ?: "사용자 게시물을 불러올 수 없습니다")
        }
    }.onFailure { exception ->
        Log.e(TAG, "사용자 게시물 조회 에러", exception)
    }

    companion object {
        private const val TAG = "CommunityRepository"
    }
}
