// app/src/main/java/com/nextread/readpick/domain/usecase/auth/LogoutUseCase.kt

package com.nextread.readpick.domain.usecase.auth

import android.util.Log
import com.nextread.readpick.data.local.TokenManager
import javax.inject.Inject

/**
 * 사용자 로그아웃 처리를 담당하는 Use Case
 *
 * 로그아웃 시 수행 작업:
 * 1. DataStore에 저장된 JWT 토큰 삭제
 * 2. 사용자 정보 (이름, 이메일, 프로필 사진 등) 삭제
 */
class LogoutUseCase @Inject constructor(
    private val tokenManager: TokenManager
) {
    companion object {
        private const val TAG = "LogoutUseCase"
    }

    /**
     * 로그아웃 로직을 실행합니다.
     *
     * @throws Exception 로그아웃 실패 시 예외 발생
     */
    suspend fun execute() {
        try {
            Log.d(TAG, "로그아웃 시작: JWT 토큰 및 사용자 정보 삭제 중...")

            // DataStore의 모든 데이터 삭제 (JWT 토큰, 사용자 정보)
            tokenManager.clear()

            Log.d(TAG, "✅ 로그아웃 성공: 모든 데이터 삭제 완료")
        } catch (e: Exception) {
            Log.e(TAG, "❌ 로그아웃 실패", e)
            throw e
        }
    }
}