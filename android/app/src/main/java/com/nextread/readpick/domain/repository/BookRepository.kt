package com.nextread.readpick.domain.repository

import com.nextread.readpick.data.model.book.BookDto
import com.nextread.readpick.data.model.common.ApiResponse
import com.nextread.readpick.data.model.search.SearchBookDto

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
}