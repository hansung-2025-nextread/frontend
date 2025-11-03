package com.nextread.readpick.data.remote.interceptor

import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 네트워크 로깅 인터셉터 제공자
 *
 * API 요청/응답을 Logcat에 출력
 * - 개발 중: BODY (전체 내용)
 * - 프로덕션: NONE (로그 없음)
 *
 * 로그 레벨:
 * - NONE: 로그 없음
 * - BASIC: 요청/응답 라인만 (예: GET /api/books 200 OK)
 * - HEADERS: 요청/응답 라인 + 헤더
 * - BODY: 전체 (요청/응답 라인 + 헤더 + 바디)
 *
 * Logcat 출력 예시:
 * ```
 * --> GET /api/books/9788936433598
 * Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
 * --> END GET
 *
 * <-- 200 OK (125ms)
 * Content-Type: application/json
 * {
 *   "isbn13": "9788936433598",
 *   "title": "1984",
 *   "author": "조지 오웰"
 * }
 * <-- END HTTP
 * ```
 *
 * 디버깅에 매우 유용! 🐛
 */
@Singleton
class LoggingInterceptorProvider @Inject constructor() {

    fun provide(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            // 개발 중에는 BODY, 프로덕션에서는 NONE으로 변경
            level = HttpLoggingInterceptor.Level.BODY

            // 프로덕션 배포 시 이렇게 변경:
            // level = if (BuildConfig.DEBUG) {
            //     HttpLoggingInterceptor.Level.BODY
            // } else {
            //     HttpLoggingInterceptor.Level.NONE
            // }
        }
    }
}
