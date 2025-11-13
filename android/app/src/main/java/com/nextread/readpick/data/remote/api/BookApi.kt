package com.nextread.readpick.data.remote.api

import com.nextread.readpick.data.model.book.BookDto
import com.nextread.readpick.data.model.book.BookListResponse
import com.nextread.readpick.data.model.common.ApiResponse
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
}