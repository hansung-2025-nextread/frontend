package com.nextread.readpick.di

import com.nextread.readpick.data.repository.AuthRepositoryImpl
import com.nextread.readpick.data.repository.OnboardingRepositoryImpl
import com.nextread.readpick.domain.repository.AuthRepository
import com.nextread.readpick.domain.repository.OnboardingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Repository 관련 의존성 주입 모듈
 *
 * Repository 인터페이스와 구현체를 바인딩
 *
 * @Binds: 인터페이스와 구현체를 연결
 * - @Inject constructor가 있는 클래스는 자동으로 생성됨
 * - Hilt가 인터페이스 타입으로 주입 요청 시 구현체를 제공
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * AuthRepository 바인딩
     *
     * AuthRepository 인터페이스를 요청하면
     * AuthRepositoryImpl 구현체를 제공
     *
     * 사용 예시:
     * ```kotlin
     * @HiltViewModel
     * class LoginViewModel @Inject constructor(
     *     private val authRepository: AuthRepository  // AuthRepositoryImpl이 주입됨
     * ) : ViewModel()
     * ```
     */
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    /**
     * OnboardingRepository 바인딩
     *
     * OnboardingRepository 인터페이스를 요청하면
     * OnboardingRepositoryImpl 구현체를 제공
     */
    @Binds
    @Singleton
    abstract fun bindOnboardingRepository(
        onboardingRepositoryImpl: OnboardingRepositoryImpl
    ): OnboardingRepository
}
