package com.nextread.readpick.domain.repository

import com.nextread.readpick.data.model.collection.CollectionBookResponse
import com.nextread.readpick.data.model.collection.UserCollectionResponse
import com.nextread.readpick.data.model.common.PageResponse

/**
 * 컬렉션 관련 Repository 인터페이스
 */
interface CollectionRepository {

    /**
     * 내 컬렉션 목록 조회
     */
    suspend fun getCollections(): List<UserCollectionResponse>

    /**
     * 새 컬렉션 생성
     *
     * @param name 컬렉션 이름
     * @param isbns 컬렉션에 추가할 책의 ISBN 목록
     */
    suspend fun createCollection(name: String, isbns: List<String>): UserCollectionResponse

    /**
     * 컬렉션 이름 수정
     *
     * @param collectionId 컬렉션 ID
     * @param newName 새로운 이름
     */
    suspend fun renameCollection(collectionId: Long, newName: String): UserCollectionResponse

    /**
     * 컬렉션 삭제
     *
     * @param collectionId 컬렉션 ID
     */
    suspend fun deleteCollection(collectionId: Long)

    /**
     * 컬렉션 내 책 목록 조회
     *
     * @param collectionId 컬렉션 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     */
    suspend fun getBooksInCollection(
        collectionId: Long,
        page: Int = 0,
        size: Int = 20
    ): PageResponse<CollectionBookResponse>

    /**
     * 컬렉션에 책 추가
     *
     * @param collectionId 컬렉션 ID
     * @param isbn13 책 ISBN
     */
    suspend fun addBookToCollection(collectionId: Long, isbn13: String)

    /**
     * 컬렉션에서 책 제거
     *
     * @param collectionId 컬렉션 ID
     * @param isbn13 책 ISBN
     */
    suspend fun removeBookFromCollection(collectionId: Long, isbn13: String)
}