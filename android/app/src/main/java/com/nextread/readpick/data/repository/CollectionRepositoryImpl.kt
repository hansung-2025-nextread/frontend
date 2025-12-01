package com.nextread.readpick.data.repository

import com.nextread.readpick.data.model.collection.CreateCollectionRequest
import com.nextread.readpick.data.model.collection.UserCollectionResponse
import com.nextread.readpick.data.remote.api.CollectionApi
import com.nextread.readpick.domain.repository.CollectionRepository
import javax.inject.Inject

class CollectionRepositoryImpl @Inject constructor(
    private val collectionApi: CollectionApi
) : CollectionRepository {

    override suspend fun getCollections(): List<UserCollectionResponse> {
        val response = collectionApi.getMyCollections()
        if (response.success && response.data != null) {
            return response.data
        } else {
            throw Exception(response.message ?: "컬렉션 목록 조회 실패")
        }
    }

    override suspend fun createCollection(name: String, isbns: List<String>): UserCollectionResponse {
        val request = CreateCollectionRequest(name = name, isbns = isbns)
        val response = collectionApi.createCollection(request)
        if (response.success && response.data != null) {
            return response.data
        } else {
            throw Exception(response.message ?: "컬렉션 생성 실패")
        }
    }
}