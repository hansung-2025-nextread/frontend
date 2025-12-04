package com.nextread.readpick.data.remote.api

import com.nextread.readpick.data.model.collection.CollectionBookResponse
import com.nextread.readpick.data.model.collection.CreateCollectionRequest
import com.nextread.readpick.data.model.collection.UpdateCollectionRequest
import com.nextread.readpick.data.model.collection.UserCollectionResponse
import com.nextread.readpick.data.model.common.ApiResponse
import com.nextread.readpick.data.model.common.PageResponse
import retrofit2.http.*

interface CollectionApi {

    /**
     * 내 컬렉션 목록 조회
     * API: GET /api/users/me/collections
     */
    @GET("api/users/me/collections")
    suspend fun getMyCollections(): ApiResponse<List<UserCollectionResponse>>

    /**
     * 새 컬렉션 생성
     * API: POST /api/users/me/collections
     */
    @POST("api/users/me/collections")
    suspend fun createCollection(@Body request: CreateCollectionRequest): ApiResponse<UserCollectionResponse>

    /**
     * 컬렉션 이름 수정
     * API: PUT /api/users/me/collections/{collectionId}
     */
    @PUT("api/users/me/collections/{collectionId}")
    suspend fun updateCollectionName(
        @Path("collectionId") collectionId: Long,
        @Body request: UpdateCollectionRequest
    ): ApiResponse<UserCollectionResponse>

    /**
     * 컬렉션 삭제
     * API: DELETE /api/users/me/collections/{collectionId}
     */
    @DELETE("api/users/me/collections/{collectionId}")
    suspend fun deleteCollection(
        @Path("collectionId") collectionId: Long
    ): ApiResponse<String>

    /**
     * 컬렉션 내 책 목록 조회
     * API: GET /api/users/me/collections/{collectionId}/books
     */
    @GET("api/users/me/collections/{collectionId}/books")
    suspend fun getBooksInCollection(
        @Path("collectionId") collectionId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): PageResponse<CollectionBookResponse>

    /**
     * 컬렉션에 책 추가
     * API: POST /api/users/me/collections/{collectionId}/books/{isbn13}
     */
    @POST("api/users/me/collections/{collectionId}/books/{isbn13}")
    suspend fun addBookToCollection(
        @Path("collectionId") collectionId: Long,
        @Path("isbn13") isbn13: String
    ): ApiResponse<String>

    /**
     * 컬렉션에서 책 제거
     * API: DELETE /api/users/me/collections/{collectionId}/books/{isbn13}
     */
    @DELETE("api/users/me/collections/{collectionId}/books/{isbn13}")
    suspend fun removeBookFromCollection(
        @Path("collectionId") collectionId: Long,
        @Path("isbn13") isbn13: String
    ): ApiResponse<String>
}