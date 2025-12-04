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
     * API: GET /v1/api/users/me/collections
     * Response: 직접 List<UserCollectionResponse> 반환 (ApiResponse 래퍼 없음)
     */
    @GET("v1/api/users/me/collections")
    suspend fun getMyCollections(): List<UserCollectionResponse>

    /**
     * 새 컬렉션 생성
     * API: POST /v1/api/users/me/collections
     * Response: 직접 UserCollectionResponse 반환 (ApiResponse 래퍼 없음)
     */
    @POST("v1/api/users/me/collections")
    suspend fun createCollection(@Body request: CreateCollectionRequest): UserCollectionResponse

    /**
     * 컬렉션 이름 수정
     * API: PUT /v1/api/users/me/collections/{collectionId}
     * Response: 직접 UserCollectionResponse 반환 (ApiResponse 래퍼 없음)
     */
    @PUT("v1/api/users/me/collections/{collectionId}")
    suspend fun updateCollectionName(
        @Path("collectionId") collectionId: Long,
        @Body request: UpdateCollectionRequest
    ): UserCollectionResponse

    /**
     * 컬렉션 삭제
     * API: DELETE /v1/api/users/me/collections/{collectionId}
     * Response: 204 No Content (응답 본문 없음)
     */
    @DELETE("v1/api/users/me/collections/{collectionId}")
    suspend fun deleteCollection(
        @Path("collectionId") collectionId: Long
    )

    /**
     * 컬렉션 내 책 목록 조회
     * API: GET /v1/api/users/me/collections/{collectionId}/books
     */
    @GET("v1/api/users/me/collections/{collectionId}/books")
    suspend fun getBooksInCollection(
        @Path("collectionId") collectionId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): PageResponse<CollectionBookResponse>

    /**
     * 컬렉션에 책 추가
     * API: POST /v1/api/users/me/collections/{collectionId}/books/{isbn13}
     * Response: 204 No Content (응답 본문 없음)
     */
    @POST("v1/api/users/me/collections/{collectionId}/books/{isbn13}")
    suspend fun addBookToCollection(
        @Path("collectionId") collectionId: Long,
        @Path("isbn13") isbn13: String
    )

    /**
     * 컬렉션에서 책 제거
     * API: DELETE /v1/api/users/me/collections/{collectionId}/books/{isbn13}
     * Response: 204 No Content (응답 본문 없음)
     */
    @DELETE("v1/api/users/me/collections/{collectionId}/books/{isbn13}")
    suspend fun removeBookFromCollection(
        @Path("collectionId") collectionId: Long,
        @Path("isbn13") isbn13: String
    )
}