package com.nextread.readpick.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

// 깃 브랜치 연습
/**
 * JWT 토큰 관리 클래스
 *
 * DataStore를 사용하여 JWT 토큰을 안전하게 저장/조회
 * - 비동기 처리 (코루틴)
 * - SharedPreferences보다 안전
 * - 타입 안전성 보장
 *
 * 사용 예시:
 * ```
 * // 토큰 저장
 * tokenManager.saveToken("eyJhbGciOiJIUzI1NiIs...")
 *
 * // 토큰 조회 (동기)
 * val token = tokenManager.getToken()
 *
 * // 토큰 조회 (비동기)
 * tokenManager.getTokenFlow().collect { token ->
 *     println(token)
 * }
 *
 * // 로그아웃
 * tokenManager.clear()
 * ```
 */

// DataStore 인스턴스 생성
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Key 정의
    companion object {
        private val JWT_TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_PICTURE_KEY = stringPreferencesKey("user_picture")
        private val USER_ROLE_KEY = stringPreferencesKey("user_role")
    }

    /**
     * JWT 토큰 저장
     *
     * @param token JWT 토큰 문자열
     *
     * 로그인 성공 시 호출:
     * ```
     * loginResponse.jwt?.let { token ->
     *     tokenManager.saveToken(token)
     * }
     * ```
     */
    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[JWT_TOKEN_KEY] = token
        }
    }

    /**
     * JWT 토큰 조회 (동기)
     *
     * @return JWT 토큰 또는 null
     *
     * ⚠️ 주의: runBlocking 사용 (Interceptor에서 필요)
     * 일반적으로는 getTokenFlow() 사용 권장
     *
     * AuthInterceptor에서 사용:
     * ```
     * val token = tokenManager.getToken()
     * if (token != null) {
     *     request.addHeader("Authorization", "Bearer $token")
     * }
     * ```
     */
    fun getToken(): String? {
        return runBlocking {
            context.dataStore.data.first()[JWT_TOKEN_KEY]
        }
    }

    /**
     * JWT 토큰 조회 (비동기 Flow)
     *
     * @return JWT 토큰 Flow
     *
     * ViewModel에서 사용 권장:
     * ```
     * tokenManager.getTokenFlow().collect { token ->
     *     if (token != null) {
     *         // 로그인 상태
     *     } else {
     *         // 로그아웃 상태
     *     }
     * }
     * ```
     */
    fun getTokenFlow(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[JWT_TOKEN_KEY]
        }
    }

    /**
     * 사용자 정보 저장
     *
     * @param email 사용자 이메일
     * @param name 사용자 이름
     * @param picture 프로필 사진 URL
     *
     * 로그인 성공 시 함께 저장:
     * ```
     * tokenManager.saveUserInfo(
     *     email = loginResponse.email,
     *     name = loginResponse.name,
     *     picture = loginResponse.picture
     * )
     * ```
     */
    suspend fun saveUserInfo(email: String, name: String, picture: String? = null) {
        context.dataStore.edit { preferences ->
            preferences[USER_EMAIL_KEY] = email
            preferences[USER_NAME_KEY] = name
            picture?.let { preferences[USER_PICTURE_KEY] = it }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // 🚨 [필수 추가] AuthRepositoryImpl에서 동기적으로 호출할 함수들 (runBlocking 사용)
    // ---------------------------------------------------------------------------------------------

    /**
     * 사용자 이메일 조회 (동기)
     */
    fun getEmail(): String? {
        return runBlocking {
            context.dataStore.data.first()[USER_EMAIL_KEY]
        }
    }

    /**
     * 사용자 이름 조회 (동기)
     */
    fun getName(): String? {
        return runBlocking {
            context.dataStore.data.first()[USER_NAME_KEY]
        }
    }

    /**
     * 사용자 프로필 사진 URL 조회 (동기)
     */
    fun getPicture(): String? {
        return runBlocking {
            context.dataStore.data.first()[USER_PICTURE_KEY]
        }
    }

    /**
     * 사용자 이메일 조회
     */
    fun getUserEmailFlow(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_EMAIL_KEY]
        }
    }

    /**
     * 사용자 이름 조회
     */
    fun getUserNameFlow(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_NAME_KEY]
        }
    }

    /**
     * 사용자 프로필 사진 URL 조회
     */
    fun getUserPictureFlow(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_PICTURE_KEY]
        }
    }

    /**
     * 사용자 역할 저장
     *
     * @param role 사용자 역할 (USER 또는 ADMIN)
     */
    suspend fun saveUserRole(role: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ROLE_KEY] = role
        }
    }

    /**
     * 사용자 역할 조회 (동기)
     *
     * @return 사용자 역할 또는 null
     */
    fun getUserRole(): String? {
        return runBlocking {
            context.dataStore.data.first()[USER_ROLE_KEY]
        }
    }

    /**
     * 사용자 역할 조회 (비동기 Flow)
     *
     * @return 사용자 역할 Flow
     */
    fun getUserRoleFlow(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_ROLE_KEY]
        }
    }

    /**
     * 관리자 여부 확인
     *
     * @return ADMIN이면 true (대소문자 무시)
     */
    fun isAdmin(): Boolean {
        return getUserRole()?.equals("ADMIN", ignoreCase = true) == true
    }

    /**
     * 모든 데이터 삭제 (로그아웃)
     *
     * 로그아웃 시 호출:
     * ```
     * tokenManager.clear()
     * navController.navigate("login") {
     *     popUpTo("home") { inclusive = true }
     * }
     * ```
     */
    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /**
     * 로그인 상태 확인
     *
     * @return 토큰이 있으면 true
     *
     * 앱 시작 시 사용:
     * ```
     * if (tokenManager.isLoggedIn()) {
     *     // 자동 로그인 → 홈 화면
     *     startDestination = "home"
     * } else {
     *     // 로그인 화면
     *     startDestination = "login"
     * }
     * ```
     */
    suspend fun isLoggedIn(): Boolean {
        return context.dataStore.data.first()[JWT_TOKEN_KEY] != null
    }
}
