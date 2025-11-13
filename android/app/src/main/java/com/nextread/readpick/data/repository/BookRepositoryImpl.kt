package com.nextread.readpick.data.repository

import android.util.Log
import com.nextread.readpick.data.model.book.BookDto
import com.nextread.readpick.data.remote.api.BookApi
import com.nextread.readpick.domain.repository.BookRepository
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val bookApi: BookApi
) : BookRepository {

    /**
     * 🚨 categoryId: Int? 파라미터 추가
     */
    override suspend fun getBestsellers(categoryId: Int?): Result<List<BookDto>> = runCatching {
        Log.d(TAG, "베스트셀러 조회 API 호출 (CategoryID: $categoryId)")

        // 🚨 categoryId 파라미터 전달
        val response = bookApi.getBestsellers(category = categoryId)

        if (response.success && response.data != null) {
            // 🚨 [수정] response.data가 List<BookDto> 그 자체입니다.
            Log.d(TAG, "베스트셀러 ${response.data.size}개 조회 성공")
            response.data // 👈 .books 제거
        } else {
            Log.e(TAG, "베스트셀러 조회 실패: ${response.message}")
            throw Exception(response.message ?: "베스트셀러를 불러올 수 없습니다")
        }
    }.onFailure { exception ->
        Log.e(TAG, "베스트셀러 조회 에러", exception)
    }

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