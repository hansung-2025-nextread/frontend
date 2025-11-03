package com.nextread.readpick.di

import android.content.Context
import com.nextread.readpick.data.local.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * DataStore 관련 의존성 주입 모듈
 *
 * 제공하는 객체:
 * - TokenManager: JWT 토큰 관리
 *
 * @Module: Hilt 모듈임을 선언
 * @InstallIn(SingletonComponent::class): 앱 전체에서 싱글톤으로 사용
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    /**
     * TokenManager 제공
     *
     * @param context Application Context (Hilt가 자동 주입)
     * @return TokenManager 싱글톤 인스턴스
     *
     * 사용 예시 (ViewModel에서):
     * ```
     * @HiltViewModel
     * class LoginViewModel @Inject constructor(
     *     private val tokenManager: TokenManager  // 자동 주입!
     * ) : ViewModel()
     * ```
     */
    @Provides
    @Singleton
    fun provideTokenManager(
        @ApplicationContext context: Context
    ): TokenManager {
        return TokenManager(context)
    }
}
