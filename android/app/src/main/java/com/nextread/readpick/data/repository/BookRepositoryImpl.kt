package com.nextread.readpick.data.repository

import android.util.Log
import com.nextread.readpick.data.model.book.BookDetailDto
import com.nextread.readpick.data.model.book.BookDto
import com.nextread.readpick.data.model.book.SavedBookDto
import com.nextread.readpick.data.model.category.CategoryDto
import com.nextread.readpick.data.model.search.SearchBookDto
import com.nextread.readpick.data.model.search.SearchLogDto
import com.nextread.readpick.data.model.search.SearchPageResponse
import com.nextread.readpick.data.model.search.SearchRequest
import com.nextread.readpick.data.model.search.SortType
import com.nextread.readpick.data.model.user.SearchHistorySettingRequest
import com.nextread.readpick.data.remote.api.BookApi
import com.nextread.readpick.domain.repository.BookRepository
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val bookApi: BookApi
) : BookRepository {

    /**
     * 베스트셀러 목록 조회
     * categoryId가 null이면 전체, 있으면 해당 카테고리 베스트셀러
     */
    override suspend fun getBestsellers(categoryId: Int?): Result<List<BookDto>> = runCatching {
        Log.d(TAG, "베스트셀러 조회 API 호출 - categoryId: $categoryId")

        val response = if (categoryId == null) {
            // 전체 베스트셀러 조회
            bookApi.getAllBestsellers(maxResults = 20)
        } else {
            // 카테고리별 베스트셀러 조회
            bookApi.getBestsellersByCategory(categoryId = categoryId, maxResults = 20)
        }

        if (response.success && response.data != null) {
            Log.d(TAG, "베스트셀러 조회 성공: ${response.data.size}개")
            response.data
        } else {
            throw Exception(response.message ?: "베스트셀러를 불러올 수 없습니다")
        }
    }.onFailure { exception ->
        Log.e(TAG, "베스트셀러 조회 에러", exception)
    }

    /**
     * 개인화 추천도서 조회
     */
    override suspend fun getPersonalizedRecommendations(limit: Int): Result<List<BookDto>> = runCatching {
        val response = bookApi.getPersonalizedRecommendations(limit)

        if (response.success && response.data != null) {
            val bookDtos = response.data.books.map { bookDetail ->
                mapBookDetailToBookDto(bookDetail)
            }
            bookDtos
        } else {
            throw Exception(response.message ?: "개인화 추천 조회 실패")
        }
    }.onFailure { exception ->
        Log.e(TAG, "개인화 추천 조회 에러", exception)
    }

    /**
     * BookDetailDto를 BookDto로 변환
     */
    private fun mapBookDetailToBookDto(detail: BookDetailDto): BookDto {
        return BookDto(
            isbn13 = detail.isbn13,
            title = detail.title,
            author = detail.author,
            cover = detail.cover,
            description = detail.description,
            categoryName = detail.categoryIdList.firstOrNull()?.toString()
        )
    }

    /**
     * 도서 상세 조회
     * 참고: 백엔드에서 ApiResponse 없이 BookDetailDto 직접 반환
     */
    override suspend fun getBookDetail(isbn13: String): Result<BookDetailDto> = runCatching {
        Log.d(TAG, "도서 상세 조회 API 호출: $isbn13")

        val bookDetail = bookApi.getBookDetail(isbn13)
        Log.d(TAG, "도서 상세 조회 성공: ${bookDetail.title}")

        bookDetail
    }.onFailure { exception ->
        Log.e(TAG, "도서 상세 조회 에러", exception)
    }

    /**
     * 도서 검색
     * (서버 응답: data 객체 안에 books 리스트가 있음)
     */
    override suspend fun searchBooks(
        keyword: String,
        sortType: SortType,
        page: Int,
        size: Int
    ): Result<SearchPageResponse> = runCatching {
        Log.d(TAG, "도서 검색 API 호출: keyword=$keyword, sort=$sortType, page=$page, size=$size")

        val request = SearchRequest(
            query = keyword,
            sortBy = sortType.value,
            page = page,
            size = size
        )
        val response = bookApi.searchBooks(request)

        if (response.success && response.data != null) {
            Log.d(TAG, "검색 결과: ${response.data.books.size}건 (페이지 ${page+1}/${response.data.page.totalPages})")
            response.data
        } else {
            throw Exception(response.message ?: "검색 결과가 없습니다.")
        }
    }.onFailure { exception ->
        Log.e(TAG, "도서 검색 에러", exception)
    }

    /**
     * 내 서재에 책 저장
     * 참고: 백엔드에서 단순 문자열 메시지 반환
     */
    override suspend fun saveBook(isbn13: String): Result<Unit> = runCatching {
        Log.d(TAG, "책 저장 API 호출: $isbn13")

        val response = bookApi.saveBook(isbn13)
        val message = response.string()
        Log.d(TAG, "책 저장 성공: $message")

        Unit
    }.onFailure { exception ->
        Log.e(TAG, "책 저장 에러", exception)
    }

    /**
     * 내 서재에서 책 삭제
     * 참고: 백엔드에서 단순 문자열 메시지 반환
     */
    override suspend fun deleteBook(isbn13: String): Result<Unit> = runCatching {
        Log.d(TAG, "책 삭제 API 호출: $isbn13")

        val response = bookApi.deleteBook(isbn13)
        val message = response.string()
        Log.d(TAG, "책 삭제 성공: $message")

        Unit
    }.onFailure { exception ->
        Log.e(TAG, "책 삭제 에러", exception)
    }

    /**
     * 내 서재 책 목록 조회
     */
    override suspend fun getSavedBooks(): Result<List<SavedBookDto>> = runCatching {
        val response = bookApi.getSavedBooks()
        response.content
    }.onFailure { exception ->
        Log.e(TAG, "내 서재 조회 에러", exception)
    }

    /**
     * 검색 기록 조회
     */
    override suspend fun getSearchHistory(): Result<List<SearchLogDto>> = runCatching {
        Log.d(TAG, "검색 기록 조회 API 호출")
        val response = bookApi.getSearchHistory()

        if (response.success && response.data != null) {
            Log.d(TAG, "검색 기록 조회 성공: ${response.data.size}건")
            response.data
        } else {
            throw Exception(response.message ?: "검색 기록을 불러올 수 없습니다")
        }
    }.onFailure { exception ->
        Log.e(TAG, "검색 기록 조회 에러", exception)
    }

    /**
     * 검색 기록 단일 삭제
     */
    override suspend fun deleteSearchLog(id: Long): Result<Unit> = runCatching {
        Log.d(TAG, "검색 기록 삭제 API 호출: $id")
        val response = bookApi.deleteSearchLog(id)

        if (response.success) {
            Log.d(TAG, "검색 기록 삭제 성공")
            Unit
        } else {
            throw Exception(response.message ?: "검색 기록을 삭제할 수 없습니다")
        }
    }.onFailure { exception ->
        Log.e(TAG, "검색 기록 삭제 에러", exception)
    }

    /**
     * 검색 기록 전체 삭제
     */
    override suspend fun clearAllSearchHistory(): Result<Unit> = runCatching {
        Log.d(TAG, "검색 기록 전체 삭제 API 호출")
        val response = bookApi.clearAllSearchHistory()

        if (response.success) {
            Log.d(TAG, "검색 기록 전체 삭제 성공")
            Unit
        } else {
            throw Exception(response.message ?: "검색 기록을 삭제할 수 없습니다")
        }
    }.onFailure { exception ->
        Log.e(TAG, "검색 기록 전체 삭제 에러", exception)
    }

    /**
     * 검색 기록 설정 조회
     */
    override suspend fun getSearchHistorySetting(): Result<Boolean> = runCatching {
        Log.d(TAG, "검색 기록 설정 조회 API 호출")
        val response = bookApi.getSearchHistorySetting()

        if (response.success && response.data != null) {
            Log.d(TAG, "검색 기록 설정: ${response.data.searchHistoryEnabled}")
            response.data.searchHistoryEnabled
        } else {
            throw Exception(response.message ?: "설정을 불러올 수 없습니다")
        }
    }.onFailure { exception ->
        Log.e(TAG, "검색 기록 설정 조회 에러", exception)
    }

    /**
     * 검색 기록 설정 변경
     */
    override suspend fun updateSearchHistorySetting(enabled: Boolean): Result<Boolean> = runCatching {
        Log.d(TAG, "검색 기록 설정 변경 API 호출: $enabled")
        val response = bookApi.updateSearchHistorySetting(SearchHistorySettingRequest(enabled))

        if (response.success && response.data != null) {
            Log.d(TAG, "검색 기록 설정 변경 성공: ${response.data.searchHistoryEnabled}")
            response.data.searchHistoryEnabled
        } else {
            throw Exception(response.message ?: "설정을 변경할 수 없습니다")
        }
    }.onFailure { exception ->
        Log.e(TAG, "검색 기록 설정 변경 에러", exception)
    }

    /**
     * 전체 카테고리 목록 조회
     */
    override suspend fun getAllCategories(): Result<List<CategoryDto>> = runCatching {
        Log.d(TAG, "전체 카테고리 조회 API 호출")
        val response = bookApi.getAllCategories()

        if (response.success && response.data != null) {
            Log.d(TAG, "카테고리 조회 성공: ${response.data.size}개")
            response.data
        } else {
            throw Exception(response.message ?: "카테고리를 불러올 수 없습니다")
        }
    }.onFailure { exception ->
        Log.e(TAG, "카테고리 조회 에러", exception)
    }

    companion object {
        private const val TAG = "BookRepository"
    }
}