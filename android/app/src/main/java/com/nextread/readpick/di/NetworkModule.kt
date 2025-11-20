package com.nextread.readpick.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.nextread.readpick.data.remote.api.BookApi
import com.nextread.readpick.data.remote.api.ChatbotApi
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

// ★ Gson 관련 임포트 모두 삭제 (com.google.gson..., com.squareup...gson...)

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val BASE_URL = com.nextread.readpick.BuildConfig.BASE_URL

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
        prettyPrint = true
    }

    // ★ provideGson() 함수 삭제! (더 이상 필요 없음)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptorProvider: LoggingInterceptorProvider
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptorProvider.provide())
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
        // ★ gson: Gson 파라미터 삭제!
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            // Kotlinx Serialization 컨버터만 남김
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            // ★ Gson 컨버터 추가하는 줄 삭제!
            .build()
    }

    // ... (나머지 API provide 함수들은 그대로 유지) ...
    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): com.nextread.readpick.data.remote.api.AuthApi {
        return retrofit.create(com.nextread.readpick.data.remote.api.AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideOnboardingApi(retrofit: Retrofit): com.nextread.readpick.data.remote.api.OnboardingApi {
        return retrofit.create(com.nextread.readpick.data.remote.api.OnboardingApi::class.java)
    }

    @Provides
    @Singleton
    fun provideBookApi(retrofit: Retrofit): BookApi {
        return retrofit.create(BookApi::class.java)
    }

    @Provides
    @Singleton
    fun provideChatbotApi(retrofit: Retrofit): ChatbotApi {
        return retrofit.create(ChatbotApi::class.java)
    }
}