package com.nextread.readpick.domain.repository

/**
 * 인증 Repository 인터페이스
 *
 * 인증 관련 데이터 소스 접근을 정의
 * (실제 구현은 data layer에서)
 */
interface AuthRepository {

    /**
     * Google 로그인
     *
     * @param idToken Google에서 받은 ID Token
     * @return Result<Unit> 성공 시 Unit, 실패 시 예외
     *
     * 성공 시: TokenManager에 JWT 저장 완료
     * 실패 시: 에러 메시지 포함
     */
    suspend fun loginWithGoogle(idToken: String): Result<Unit>

    /**
     * 로그아웃
     *
     * TokenManager의 모든 데이터 삭제
     */
    suspend fun logout(): Result<Unit>
}
