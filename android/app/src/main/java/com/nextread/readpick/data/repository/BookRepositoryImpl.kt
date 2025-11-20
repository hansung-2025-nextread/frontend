package com.nextread.readpick.data.repository

import android.util.Log
import com.nextread.readpick.data.model.book.BookDto
import com.nextread.readpick.data.model.search.SearchBookDto
import com.nextread.readpick.data.model.search.SearchRequest
import com.nextread.readpick.data.remote.api.BookApi
import com.nextread.readpick.domain.repository.BookRepository
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val bookApi: BookApi
) : BookRepository {

    /**
     * 베스트셀러 목록 조회
     * (서버 응답: data 자체가 List<BookDto>임)
     */
    override suspend fun getBestsellers(categoryId: Int?): Result<List<BookDto>> = runCatching {
        // Log.d(TAG, "베스트셀러 조회 API 호출 (CategoryID: $categoryId)") // 필요 시 주석 해제

        val response = bookApi.getBestsellers(category = categoryId)

        if (response.success && response.data != null) {
            // 🚨 [확인됨] response.data가 이미 List이므로 바로 반환
            response.data
        } else {
            throw Exception(response.message ?: "베스트셀러를 불러올 수 없습니다")
        }
    }.onFailure { exception ->
        Log.e(TAG, "베스트셀러 조회 에러", exception)
    }

    /**
     * 도서 상세 조회
     */
    override suspend fun getBookDetail(isbn13: String): Result<BookDto> = runCatching {
        Log.d(TAG, "도서 상세 조회 API 호출: $isbn13")

        val response = bookApi.getBookDetail(isbn13)

        if (response.success && response.data != null) {
            Log.d(TAG, "도서 상세 조회 성공: ${response.data.title}")
            response.data
        } else {
            throw Exception(response.message ?: "도서 정보를 불러올 수 없습니다")
        }
    }.onFailure { exception ->
        Log.e(TAG, "도서 상세 조회 에러", exception)
    }

    /**
     * 도서 검색
     * (서버 응답: data 객체 안에 books 리스트가 있음)
     */
    override suspend fun searchBooks(keyword: String): Result<List<SearchBookDto>> = runCatching {
        Log.d(TAG, "도서 검색 API 호출: $keyword")

        // 🚨 [수정] 검색어를 Request 객체로 감싸서 전달
        val request = SearchRequest(query = keyword)
        val response = bookApi.searchBooks(request)

        if (response.success && response.data != null) {
            Log.d(TAG, "검색 결과: ${response.data.books.size}건")
            response.data.books
        } else {
            throw Exception(response.message ?: "검색 결과가 없습니다.")
        }
    }.onFailure { exception ->
        Log.e(TAG, "도서 검색 에러", exception)
    }

    /**
     * 내 서재에 책 저장
     */
    override suspend fun saveBook(isbn13: String): Result<Unit> = runCatching {
        Log.d(TAG, "책 저장 API 호출: $isbn13")

        val response = bookApi.saveBook(isbn13)

        if (response.success) {
            Log.d(TAG, "책 저장 성공")
            Unit
        } else {
            throw Exception(response.message ?: "책을 저장할 수 없습니다")
        }
    }.onFailure { exception ->
        Log.e(TAG, "책 저장 에러", exception)
    }

    companion object {
        private const val TAG = "BookRepository"
    }
}