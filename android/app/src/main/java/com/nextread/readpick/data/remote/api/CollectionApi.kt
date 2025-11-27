package com.nextread.readpick.data.remote.api

import com.nextread.readpick.data.model.collection.CreateCollectionRequest
import com.nextread.readpick.data.model.collection.UserCollectionResponse
import com.nextread.readpick.data.model.common.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CollectionApi {

    /**
     * 내 컬렉션 목록 조회
     * API: GET /v1/api/users/me/collections
     */
    @GET("v1/api/users/me/collections") // 🚨 [수정 완료] v1 접두사 추가
    suspend fun getMyCollections(): ApiResponse<List<UserCollectionResponse>>

    /**
     * 새 컬렉션 생성
     * API: POST /v1/api/users/me/collections
     */
    @POST("v1/api/users/me/collections") // 🚨 [수정 완료] v1 접두사 추가
    suspend fun createCollection(@Body request: CreateCollectionRequest): ApiResponse<UserCollectionResponse>
}