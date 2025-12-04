package com.nextread.readpick.data.repository

import com.nextread.readpick.data.model.collection.CollectionBookResponse
import com.nextread.readpick.data.model.collection.CreateCollectionRequest
import com.nextread.readpick.data.model.collection.UpdateCollectionRequest
import com.nextread.readpick.data.model.collection.UserCollectionResponse
import com.nextread.readpick.data.model.common.PageResponse
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

    override suspend fun renameCollection(collectionId: Long, newName: String): UserCollectionResponse {
        val request = UpdateCollectionRequest(name = newName)
        val response = collectionApi.updateCollectionName(collectionId, request)
        if (response.success && response.data != null) {
            return response.data
        } else {
            throw Exception(response.message ?: "컬렉션 이름 수정 실패")
        }
    }

    override suspend fun deleteCollection(collectionId: Long) {
        val response = collectionApi.deleteCollection(collectionId)
        if (!response.success) {
            throw Exception(response.message ?: "컬렉션 삭제 실패")
        }
    }

    override suspend fun getBooksInCollection(
        collectionId: Long,
        page: Int,
        size: Int
    ): PageResponse<CollectionBookResponse> {
        return collectionApi.getBooksInCollection(collectionId, page, size)
    }

    override suspend fun addBookToCollection(collectionId: Long, isbn13: String) {
        val response = collectionApi.addBookToCollection(collectionId, isbn13)
        if (!response.success) {
            throw Exception(response.message ?: "책 추가 실패")
        }
    }

    override suspend fun removeBookFromCollection(collectionId: Long, isbn13: String) {
        val response = collectionApi.removeBookFromCollection(collectionId, isbn13)
        if (!response.success) {
            throw Exception(response.message ?: "책 제거 실패")
        }
    }
}