// app/src/main/java/com/nextread/readpick/domain/usecase/auth/LogoutUseCase.kt

package com.nextread.readpick.domain.usecase.auth

import javax.inject.Inject

/**
 * 사용자 로그아웃 처리를 담당하는 Use Case.
 * Use Case는 비즈니스 로직을 포함하며 Repositoy에 의존합니다.
 */
class LogoutUseCase @Inject constructor(
    // private val authRepository: AuthRepository // TODO: Repository 의존성 주입 (필요시)
) {
    /**
     * 로그아웃 로직을 실행합니다.
     */
    suspend fun execute() {
        // TODO: 실제 토큰 삭제 및 서버 로그아웃 API 호출 로직 구현
        println("로그아웃 실행됨") // 임시 로그
    }
}