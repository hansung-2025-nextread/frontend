package com.nextread.readpick.domain.repository

import com.nextread.readpick.data.model.collection.CreateCollectionRequest
import com.nextread.readpick.data.model.collection.UserCollectionResponse

interface CollectionRepository {
    suspend fun getCollections(): List<UserCollectionResponse>
    suspend fun createCollection(name: String, isbns: List<String>): UserCollectionResponse
}