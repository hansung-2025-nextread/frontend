package com.nextread.readpick.di

// 👇 1. Import 확인 (빨간줄이 뜨면 Alt+Enter로 import 해주세요)
import com.nextread.readpick.data.repository.AdminRepositoryImpl
import com.nextread.readpick.data.repository.AuthRepositoryImpl
import com.nextread.readpick.data.repository.BookRepositoryImpl
import com.nextread.readpick.data.repository.ChatbotRepositoryImpl
import com.nextread.readpick.data.repository.OnboardingRepositoryImpl // ✅ 추가됨

import com.nextread.readpick.domain.repository.AdminRepository
import com.nextread.readpick.domain.repository.AuthRepository
import com.nextread.readpick.domain.repository.BookRepository
import com.nextread.readpick.domain.repository.ChatbotRepository
import com.nextread.readpick.domain.repository.OnboardingRepository // ✅ 추가됨

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    // 🔑 1. 로그인/인증 (Auth)
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    // 🚀 2. 온보딩 (Onboarding - 이번 에러 해결!)
    @Binds
    @Singleton
    abstract fun bindOnboardingRepository(
        onboardingRepositoryImpl: OnboardingRepositoryImpl
    ): OnboardingRepository

    // 📚 3. 도서 (Book)
    @Binds
    @Singleton
    abstract fun bindBookRepository(
        bookRepositoryImpl: BookRepositoryImpl
    ): BookRepository

    // 🛡️ 5. 관리자 (Admin)
    @Binds
    @Singleton
    abstract fun bindAdminRepository(
        adminRepositoryImpl: AdminRepositoryImpl
    ): AdminRepository

    @Binds
    @Singleton
    abstract  fun bindChatbotRepository(
        chatbotRepositoryImpl: ChatbotRepositoryImpl
    ): ChatbotRepository
}