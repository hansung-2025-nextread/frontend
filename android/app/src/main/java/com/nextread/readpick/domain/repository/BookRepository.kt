package com.nextread.readpick.domain.repository

import com.nextread.readpick.data.model.book.BookDto
import com.nextread.readpick.data.model.book.BookDetailDto
import com.nextread.readpick.data.model.book.SavedBookDto
import com.nextread.readpick.data.model.category.CategoryDto
import com.nextread.readpick.data.model.search.SearchBookDto
import com.nextread.readpick.data.model.search.SearchLogDto
import com.nextread.readpick.data.model.search.SearchPageResponse
import com.nextread.readpick.data.model.search.SortType

interface BookRepository {

    /**
     * 전체 베스트셀러 목록 조회
     */
    suspend fun getBestsellers(categoryId: Int? = null): Result<List<BookDto>>

    /**
     * 개인화 추천도서 조회
     */
    suspend fun getPersonalizedRecommendations(limit: Int = 15): Result<List<BookDto>>

    /**
     * 도서 상세 조회
     */
    suspend fun getBookDetail(isbn13: String): Result<BookDetailDto>

    /**
     * 내 서재에 저장
     */
    suspend fun saveBook(isbn13: String): Result<Unit>

    /**
     * 내 서재에서 삭제
     */
    suspend fun deleteBook(isbn13: String): Result<Unit>

    /**
     * 도서 검색 (정렬 및 페이지네이션 지원)
     */
    suspend fun searchBooks(
        keyword: String,
        sortType: SortType = SortType.ACCURACY,
        page: Int = 0,
        size: Int = 20
    ): Result<SearchPageResponse>

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

    /**
     * 전체 카테고리 목록 조회
     */
    suspend fun getAllCategories(): Result<List<CategoryDto>>
}