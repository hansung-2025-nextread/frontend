package com.nextread.readpick.domain.repository

import com.nextread.readpick.data.model.book.BookDto
import com.nextread.readpick.data.model.book.SavedBookDto
import com.nextread.readpick.data.model.search.SearchBookDto
import com.nextread.readpick.data.model.search.SearchLogDto

interface BookRepository {

    /**
     * 베스트셀러 목록 조회
     * 🚨 categoryId: Int? 파라미터 추가
     */
    suspend fun getBestsellers(categoryId: Int? = null): Result<List<BookDto>>

    /**
     * 도서 상세 조회
     */
    suspend fun getBookDetail(isbn13: String): Result<BookDto>

    /**
     * 내 서재에 저장
     */
    suspend fun saveBook(isbn13: String): Result<Unit>

    // 반환 타입을 List<SearchBookDto>로 설정
    suspend fun searchBooks(keyword: String): Result<List<SearchBookDto>>

    /**
     * 내 서재 책 목록 조회
     */
    suspend fun getSavedBooks(): Result<List<SavedBookDto>>

    /**
     * 검색 기록 조회
     */
    suspend fun getSearchHistory(): Result<List<SearchLogDto>>

    /**
     * 검색 기록 단일 삭제
     */
    suspend fun deleteSearchLog(id: Long): Result<Unit>

    /**
     * 검색 기록 전체 삭제
     */
    suspend fun clearAllSearchHistory(): Result<Unit>

    /**
     * 검색 기록 설정 조회
     */
    suspend fun getSearchHistorySetting(): Result<Boolean>

    /**
     * 검색 기록 설정 변경
     */
    suspend fun updateSearchHistorySetting(enabled: Boolean): Result<Boolean>
}