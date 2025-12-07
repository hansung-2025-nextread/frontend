package com.nextread.readpick.data.remote.api

import com.nextread.readpick.data.model.book.BookDto
import com.nextread.readpick.data.model.book.BookDetailDto
import com.nextread.readpick.data.model.book.PersonalizedRecommendationResponse
import com.nextread.readpick.data.model.book.SavedBookPageResponse
import com.nextread.readpick.data.model.book.UpdateReadingStatusRequest
import com.nextread.readpick.data.model.category.CategoryDto
import com.nextread.readpick.data.model.common.ApiResponse
import com.nextread.readpick.data.model.search.SearchLogDto
import com.nextread.readpick.data.model.search.SearchPageResponse
import com.nextread.readpick.data.model.search.SearchRequest
import com.nextread.readpick.data.model.search.SearchResponseData
import com.nextread.readpick.data.model.user.SearchHistorySettingRequest
import com.nextread.readpick.data.model.user.SearchHistorySettingResponse
import okhttp3.ResponseBody
import retrofit2.http.*

interface BookApi {

    /**
     * 전체 베스트셀러 목록 조회
     */
    @GET("v1/api/books/bestsellers/all")
    suspend fun getAllBestsellers(
        @Query("maxResults") maxResults: Int = 20
    ): ApiResponse<List<BookDto>>

    /**
     * 카테고리별 베스트셀러 목록 조회
     */
    @GET("v1/api/books/bestsellers")
    suspend fun getBestsellersByCategory(
        @Query("categoryId") categoryId: Int,
        @Query("maxResults") maxResults: Int = 20
    ): ApiResponse<List<BookDto>>

    /**
     * 개인화 추천도서 조회
     */
    @GET("v1/api/personalized/recommendations")
    suspend fun getPersonalizedRecommendations(
        @Query("limit") limit: Int = 15
    ): ApiResponse<PersonalizedRecommendationResponse>

    /**
     * 도서 상세 조회
     * 참고: 백엔드에서 ApiResponse로 감싸지 않고 BookDetailDto 직접 반환
     */
    @GET("v1/api/books/{isbn13}")
    suspend fun getBookDetail(
        @Path("isbn13") isbn13: String
    ): BookDetailDto

    /**
     * 내 서재에 책 저장
     * 참고: 백엔드에서 단순 문자열 메시지 반환 (JSON 아님)
     * ResponseBody를 사용하여 JSON 파싱 우회
     */
    @POST("v1/api/books/{isbn13}")
    suspend fun saveBook(
        @Path("isbn13") isbn13: String
    ): ResponseBody

    /**
     * 내 서재에서 책 삭제
     * 참고: 백엔드에서 단순 문자열 메시지 반환 (JSON 아님)
     * ResponseBody를 사용하여 JSON 파싱 우회
     */
    @DELETE("v1/api/books/{isbn13}")
    suspend fun deleteBook(
        @Path("isbn13") isbn13: String
    ): ResponseBody

    /**
     * 🚨 [수정] 도서 검색 API
     * 1. GET -> POST 변경
     * 2. 주소: "api/search/smart" (명세서 기준)
     * 3. 파라미터: @Body 사용
     * 4. 정렬 및 페이지네이션 지원
     */
    @POST("v1/api/search/smart")
    suspend fun searchBooks(
        @Body request: SearchRequest
    ): ApiResponse<SearchPageResponse>

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

    /**
     * 전체 카테고리 목록 조회
     */
    @GET("v1/api/categories")
    suspend fun getAllCategories(): ApiResponse<List<CategoryDto>>

    /**
     * 독서 상태 업데이트
     */
    @PUT("v1/api/books/saved/{isbn13}/status")
    suspend fun updateReadingStatus(
        @Path("isbn13") isbn13: String,
        @Body request: UpdateReadingStatusRequest
    ): ApiResponse<Unit>
}