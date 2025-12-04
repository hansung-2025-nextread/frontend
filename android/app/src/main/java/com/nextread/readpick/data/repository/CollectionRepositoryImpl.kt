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
        // API가 직접 List를 반환 (ApiResponse 래퍼 없음)
        return collectionApi.getMyCollections()
    }

    override suspend fun createCollection(name: String, isbns: List<String>): UserCollectionResponse {
        // 1단계: 컬렉션 생성 (이름만)
        val request = CreateCollectionRequest(name = name)
        val createdCollection = collectionApi.createCollection(request)

        // 2단계: 책이 있으면 개별적으로 추가
        if (isbns.isNotEmpty()) {
            isbns.forEach { isbn ->
                try {
                    addBookToCollection(createdCollection.id, isbn)
                } catch (e: Exception) {
                    // 책 추가 실패 시 로그만 남기고 계속 진행
                    android.util.Log.w("CollectionRepository", "책 추가 실패: $isbn", e)
                }
            }
        }

        return createdCollection
    }

    override suspend fun renameCollection(collectionId: Long, newName: String): UserCollectionResponse {
        val request = UpdateCollectionRequest(name = newName)
        return collectionApi.updateCollectionName(collectionId, request)
    }

    override suspend fun deleteCollection(collectionId: Long) {
        // API가 204 No Content 반환 (응답 본문 없음)
        collectionApi.deleteCollection(collectionId)
    }

    override suspend fun getBooksInCollection(
        collectionId: Long,
        page: Int,
        size: Int
    ): PageResponse<CollectionBookResponse> {
        return collectionApi.getBooksInCollection(collectionId, page, size)
    }

    override suspend fun addBookToCollection(collectionId: Long, isbn13: String) {
        // API가 204 No Content 반환 (응답 본문 없음)
        collectionApi.addBookToCollection(collectionId, isbn13)
    }

    override suspend fun removeBookFromCollection(collectionId: Long, isbn13: String) {
        // API가 204 No Content 반환 (응답 본문 없음)
        collectionApi.removeBookFromCollection(collectionId, isbn13)
    }
}