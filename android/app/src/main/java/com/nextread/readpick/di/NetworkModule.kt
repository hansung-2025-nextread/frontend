package com.nextread.readpick.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.nextread.readpick.data.remote.api.AdminApi
import com.nextread.readpick.data.remote.api.BookApi
import com.nextread.readpick.data.remote.api.ChatbotApi
import com.nextread.readpick.data.remote.api.CollectionApi
import com.nextread.readpick.data.remote.api.CommunityApi
import com.nextread.readpick.data.remote.api.ReviewApi
import com.nextread.readpick.data.remote.interceptor.AuthInterceptor
import com.nextread.readpick.data.remote.interceptor.LoggingInterceptorProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * 네트워크 관련 의존성 주입 모듈
 *
 * 제공하는 객체:
 * - Json: Kotlinx Serialization 설정
 * - OkHttpClient: HTTP 클라이언트 (Interceptor 포함)
 * - Retrofit: REST API 클라이언트
 * - API 인터페이스들 (팀원들이 추가)
 *
 * @Module: Hilt 모듈임을 선언
 * @InstallIn(SingletonComponent::class): 앱 전체에서 싱글톤으로 사용
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Base URL - local.properties에서 읽어옴
    // local.properties에 BASE_URL을 설정하거나, 기본값 사용
    private val BASE_URL = com.nextread.readpick.BuildConfig.BASE_URL

    /**
     * Json 설정 제공
     *
     * Kotlinx Serialization 설정
     * - ignoreUnknownKeys: 서버에서 추가 필드가 와도 에러 안 남
     * - coerceInputValues: null 값 처리 유연하게
     * - isLenient: 엄격하지 않은 JSON 파싱
     * - prettyPrint: 예쁘게 출력 (디버깅용)
     */
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true  // 모르는 키는 무시
        coerceInputValues = true  // null 값 처리
        isLenient = true          // 엄격하지 않은 JSON 파싱
        prettyPrint = true        // 예쁘게 출력 (디버깅용)
    }

    /**
     * OkHttpClient 제공
     *
     * HTTP 통신을 실제로 처리하는 클라이언트
     * - AuthInterceptor: JWT 토큰 자동 추가
     * - LoggingInterceptor: 네트워크 로그
     * - connectTimeout: 연결 타임아웃 (10초)
     * - readTimeout: 읽기 타임아웃 (30초)
     * - writeTimeout: 쓰기 타임아웃 (30초)
     *
     * @param authInterceptor JWT 토큰 자동 추가
     * @param loggingInterceptorProvider 네트워크 로그
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptorProvider: LoggingInterceptorProvider
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)           // JWT 토큰 자동 추가
            .addInterceptor(loggingInterceptorProvider.provide())  // 로깅
            .connectTimeout(10, TimeUnit.SECONDS)      // 연결 타임아웃
            .readTimeout(30, TimeUnit.SECONDS)         // 읽기 타임아웃
            .writeTimeout(30, TimeUnit.SECONDS)        // 쓰기 타임아웃
            .build()
    }

    /**
     * Retrofit 제공
     *
     * REST API 호출을 위한 Retrofit 인스턴스
     * - baseUrl: API 기본 URL
     * - client: OkHttpClient (Interceptor 포함)
     * - converterFactory: JSON ↔ Kotlin 객체 변환
     *
     * @param okHttpClient OkHttp 클라이언트
     * @param json Kotlinx Serialization 설정
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()
    }

    /**
     * AuthApi 제공
     *
     * 인증 관련 API 인터페이스
     */
    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): com.nextread.readpick.data.remote.api.AuthApi {
        return retrofit.create(com.nextread.readpick.data.remote.api.AuthApi::class.java)
    }

    /**
     * OnboardingApi 제공
     *
     * 온보딩 관련 API 인터페이스
     * - 온보딩 상태 확인
     * - 카테고리 목록 조회
     * - 카테고리 선택 제출
     */
    @Provides
    @Singleton
    fun provideOnboardingApi(retrofit: Retrofit): com.nextread.readpick.data.remote.api.OnboardingApi {
        return retrofit.create(com.nextread.readpick.data.remote.api.OnboardingApi::class.java)
    }

    /**
     * BookApi 제공 - 추가!
     */
    @Provides
    @Singleton
    fun provideBookApi(retrofit: Retrofit): BookApi {
        return retrofit.create(BookApi::class.java)
    }

    /**
     * AdminApi 제공
     *
     * 관리자 API 인터페이스
     * - 신고된 리뷰 관리
     * - 사용자 정지/해제
     */
    @Provides
    @Singleton
    fun provideAdminApi(retrofit: Retrofit): AdminApi {
        return retrofit.create(AdminApi::class.java)
    }

    /**
     * CommunityApi 제공
     *
     * 커뮤니티 API 인터페이스
     * - 게시물 조회/작성/삭제
     * - 댓글 조회/작성/삭제
     * - 좋아요
     */
    @Provides
    @Singleton
    fun provideCommunityApi(retrofit: Retrofit): CommunityApi {
        return retrofit.create(CommunityApi::class.java)
    }

    /**
     * ChatbotApi 제공
     *
     * 챗봇 대화 세션 및 메시지 관련 API 인터페이스
     * - 대화 세션 생성/조회/삭제
     * - 메시지 전송/수신
     */
    @Provides
    @Singleton
    fun provideChatbotApi(retrofit: Retrofit): ChatbotApi {
        return retrofit.create(ChatbotApi::class.java)
    }

    /**
     * CollectionApi 제공
     *
     * 컬렉션(내 책장) 관련 API 인터페이스
     * - 컬렉션 생성/조회/수정/삭제
     * - 컬렉션에 책 추가/삭제
     */
    @Provides
    @Singleton
    fun provideCollectionApi(retrofit: Retrofit): CollectionApi {
        return retrofit.create(CollectionApi::class.java)
    }

    /**
     * ReviewApi 제공
     *
     * 리뷰 관련 API 인터페이스
     * - 리뷰 조회/작성/수정/삭제
     * - 리뷰 신고
     */
    @Provides
    @Singleton
    fun provideReviewApi(retrofit: Retrofit): ReviewApi {
        return retrofit.create(ReviewApi::class.java)
    }
}
