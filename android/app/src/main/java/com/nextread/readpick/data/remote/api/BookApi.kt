package com.nextread.readpick.data.remote.api

import com.nextread.readpick.data.model.book.BookDto
import com.nextread.readpick.data.model.book.SavedBookPageResponse
import com.nextread.readpick.data.model.common.ApiResponse
import com.nextread.readpick.data.model.search.SearchLogDto
import com.nextread.readpick.data.model.search.SearchRequest
import com.nextread.readpick.data.model.search.SearchResponseData
import com.nextread.readpick.data.model.user.SearchHistorySettingRequest
import com.nextread.readpick.data.model.user.SearchHistorySettingResponse
import retrofit2.http.*

interface BookApi {

    /**
     * 베스트셀러 목록 조회
     */
    @GET("v1/api/books/bestsellers")
    suspend fun getBestsellers(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20,
        @Query("categoryId") category: Int? = null,
    ): ApiResponse<List<BookDto>>

    /**
     * 도서 상세 조회
     */
    @GET("v1/api/books/{isbn13}")
    suspend fun getBookDetail(
        @Path("isbn13") isbn13: String
    ): ApiResponse<BookDto>

    /**
     * 내 서재에 책 저장
     */
    @POST("v1/api/books/{isbn13}")
    suspend fun saveBook(
        @Path("isbn13") isbn13: String
    ): ApiResponse<Unit>

    /**
     * 🚨 [수정] 도서 검색 API
     * 1. GET -> POST 변경
     * 2. 주소: "api/search/smart" (명세서 기준)
     * 3. 파라미터: @Body 사용
     */
    @POST("v1/api/search/smart")
    suspend fun searchBooks(
        @Body request: SearchRequest
    ): ApiResponse<SearchResponseData>

    /**
     * 내 서재 책 목록 조회
     * 참고: 백엔드에서 ApiResponse로 감싸지 않고 Page 직접 반환
     */
    @GET("v1/api/users/me/saved-books")
    suspend fun getSavedBooks(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 100
    ): SavedBookPageResponse

    /**
     * 검색 기록 조회
     */
    @GET("v1/api/users/me/search-history")
    suspend fun getSearchHistory(): ApiResponse<List<SearchLogDto>>

    /**
     * 검색 기록 단일 삭제
     */
    @DELETE("v1/api/users/me/search-history/{id}")
    suspend fun deleteSearchLog(
        @Path("id") id: Long
    ): ApiResponse<Unit>

    /**
     * 검색 기록 전체 삭제
     */
    @DELETE("v1/api/users/me/search-history")
    suspend fun clearAllSearchHistory(): ApiResponse<Unit>

    /**
     * 검색 기록 설정 조회
     */
    @GET("v1/api/users/me/settings/search-history")
    suspend fun getSearchHistorySetting(): ApiResponse<SearchHistorySettingResponse>

    /**
     * 검색 기록 설정 변경
     */
    @PATCH("v1/api/users/me/settings/search-history")
    suspend fun updateSearchHistorySetting(
        @Body request: SearchHistorySettingRequest
    ): ApiResponse<SearchHistorySettingResponse>
}