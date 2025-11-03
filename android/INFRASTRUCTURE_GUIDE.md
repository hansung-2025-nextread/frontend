# ReadPick 공통 인프라 구축 가이드

> 팀장이 먼저 구축해야 할 핵심 인프라 완벽 가이드

## 🎯 인프라란?

**인프라 (Infrastructure)**: 모든 팀원이 사용할 **공통 기반 시스템**

```
인프라 = 집의 기초 공사
팀원들 작업 = 각 방 인테리어

기초가 튼튼해야 → 인테리어가 쉽다!
```

---

## 📋 인프라 구성 요소

### 1. 네트워크 레이어
```
Retrofit     → API 호출
OkHttp       → HTTP 통신
Interceptor  → JWT 자동 추가, 로깅
```

### 2. 데이터 관리
```
DataStore    → JWT 토큰 저장
TokenManager → 토큰 관리
```

### 3. 의존성 주입
```
Hilt         → 객체 자동 생성/관리
NetworkModule     → 네트워크 관련
DataStoreModule   → 저장소 관련
RepositoryModule  → Repository 관련
```

### 4. 공통 코드
```
공통 DTO          → 모든 API 응답의 기본
에러 처리         → 통일된 에러 처리
공통 UI 컴포넌트  → 재사용 가능한 UI
```

---

## 🛠️ 구축 순서

### Step 1: Application 클래스 (Hilt 설정)
### Step 2: DataStore (토큰 저장소)
### Step 3: OkHttp Interceptor (JWT 자동 추가)
### Step 4: Retrofit 설정
### Step 5: Hilt DI 모듈
### Step 6: 공통 DTO
### Step 7: 공통 UI 컴포넌트

---

## Step 1: Application 클래스 생성

### 📁 파일: `ReadPickApplication.kt`
**위치**: `app/src/main/java/com/nextread/readpick/`

```kotlin
package com.nextread.readpick

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * ReadPick 애플리케이션 클래스
 *
 * @HiltAndroidApp: Hilt 의존성 주입을 위한 필수 어노테이션
 * - 앱이 시작될 때 Hilt 컴포넌트를 초기화
 * - 모든 DI 모듈의 시작점
 */
@HiltAndroidApp
class ReadPickApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // 필요한 초기화 코드 (현재는 Hilt가 자동 처리)
    }
}
```

### 📝 AndroidManifest.xml 수정
**위치**: `app/src/main/AndroidManifest.xml`

```xml
<manifest>
    <application
        android:name=".ReadPickApplication"
        <!-- 이 줄 추가! ↑ -->
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/Theme.NextRead">

        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

### ❓ 왜 필요한가?
```
@HiltAndroidApp 없으면 → Hilt가 작동 안 함
AndroidManifest에 등록 안 하면 → Application 클래스가 실행 안 됨
```

---

## Step 2: DataStore (토큰 저장소)

### 📁 파일: `TokenManager.kt`
**위치**: `data/local/TokenManager.kt`

```kotlin
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

/**
 * JWT 토큰 관리 클래스
 *
 * DataStore를 사용하여 JWT 토큰을 안전하게 저장/조회
 * - 비동기 처리 (코루틴)
 * - SharedPreferences보다 안전
 * - 타입 안전성 보장
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
    }

    /**
     * JWT 토큰 저장
     *
     * @param token JWT 토큰 문자열
     *
     * 사용 예시:
     * tokenManager.saveToken("eyJhbGciOiJIUzI1NiIs...")
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
     * 사용 예시:
     * tokenManager.getTokenFlow().collect { token ->
     *     println(token)
     * }
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
     */
    suspend fun saveUserInfo(email: String, name: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_EMAIL_KEY] = email
            preferences[USER_NAME_KEY] = name
        }
    }

    /**
     * 모든 데이터 삭제 (로그아웃)
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
     */
    suspend fun isLoggedIn(): Boolean {
        return context.dataStore.data.first()[JWT_TOKEN_KEY] != null
    }
}
```

### ❓ 왜 필요한가?
```
문제: JWT 토큰을 어디에 저장?
해결: DataStore에 안전하게 저장

문제: 앱 재시작 시 로그인 유지?
해결: TokenManager.isLoggedIn() 체크

문제: API 호출마다 토큰 추가?
해결: Interceptor가 자동으로 추가 (다음 단계)
```

---

## Step 3: OkHttp Interceptor (JWT 자동 추가)

### 📁 파일: `AuthInterceptor.kt`
**위치**: `data/remote/interceptor/AuthInterceptor.kt`

```kotlin
package com.nextread.readpick.data.remote.interceptor

import com.nextread.readpick.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * 인증 인터셉터
 *
 * 모든 API 요청에 JWT 토큰을 자동으로 추가
 *
 * 동작 방식:
 * 1. API 호출 전 intercept() 실행
 * 2. TokenManager에서 토큰 조회
 * 3. Authorization 헤더에 "Bearer {토큰}" 추가
 * 4. 실제 API 호출
 *
 * 예시:
 * GET /api/books/123
 * Header: Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
 */
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // 원본 요청 가져오기
        val originalRequest = chain.request()

        // 토큰 조회
        val token = tokenManager.getToken()

        // 토큰이 없으면 원본 요청 그대로 진행
        if (token.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }

        // 새 요청 생성 (Authorization 헤더 추가)
        val newRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        // 새 요청으로 API 호출
        return chain.proceed(newRequest)
    }
}
```

### 📁 파일: `LoggingInterceptor.kt` (선택)
**위치**: `data/remote/interceptor/LoggingInterceptor.kt`

```kotlin
package com.nextread.readpick.data.remote.interceptor

import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 네트워크 로깅 인터셉터
 *
 * API 요청/응답을 Logcat에 출력
 * - 개발 중: BODY (전체 내용)
 * - 프로덕션: NONE (로그 없음)
 */
@Singleton
class LoggingInterceptorProvider @Inject constructor() {

    fun provide(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
            // Level.NONE    - 로그 없음
            // Level.BASIC   - 요청/응답 라인만
            // Level.HEADERS - 요청/응답 라인 + 헤더
            // Level.BODY    - 전체 (요청/응답 라인 + 헤더 + 바디)
        }
    }
}
```

### ❓ 왜 필요한가?
```
문제: 매번 API 호출할 때 토큰 추가?
해결: AuthInterceptor가 자동 추가!

Before (인터셉터 없이):
bookApi.getBookDetail(isbn13, "Bearer $token")  // 매번 토큰 추가 😰

After (인터셉터 사용):
bookApi.getBookDetail(isbn13)  // 토큰 자동 추가! 😎
```

---

## Step 4: Retrofit 설정

### 📁 파일: `NetworkModule.kt`
**위치**: `di/NetworkModule.kt`

```kotlin
package com.nextread.readpick.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.nextread.readpick.data.remote.api.AuthApi
import com.nextread.readpick.data.remote.api.BookApi
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
 * - API 인터페이스들 (AuthApi, BookApi 등)
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Base URL (실제 서버 주소로 변경 필요)
    private const val BASE_URL = "https://api.readpick.com/"  // TODO: 실제 서버 URL로 변경

    /**
     * Json 설정
     *
     * Kotlinx Serialization 설정
     * - ignoreUnknownKeys: 서버에서 추가 필드가 와도 에러 안 남
     * - coerceInputValues: null 값 처리 유연하게
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
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    /**
     * BookApi 제공
     *
     * 도서 관련 API 인터페이스
     */
    @Provides
    @Singleton
    fun provideBookApi(retrofit: Retrofit): BookApi {
        return retrofit.create(BookApi::class.java)
    }

    // TODO: 다른 API 인터페이스도 여기에 추가
    // @Provides
    // @Singleton
    // fun provideChatbotApi(retrofit: Retrofit): ChatbotApi {
    //     return retrofit.create(ChatbotApi::class.java)
    // }
}
```

### ❓ 왜 필요한가?
```
문제: Retrofit, OkHttp를 매번 생성?
해결: Hilt가 싱글톤으로 자동 생성

문제: API 인터페이스를 어떻게 만들?
해결: Retrofit.create()로 자동 생성

문제: 여러 곳에서 같은 Retrofit 인스턴스 사용?
해결: @Singleton으로 하나만 생성
```

---

## Step 5: DataStore 모듈

### 📁 파일: `DataStoreModule.kt`
**위치**: `di/DataStoreModule.kt`

```kotlin
package com.nextread.readpick.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
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
 * - DataStore<Preferences>: 키-값 저장소
 * - TokenManager: JWT 토큰 관리
 */

// DataStore 확장 프로퍼티
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    /**
     * DataStore 제공
     *
     * @param context Application Context
     * @return DataStore<Preferences> 인스턴스
     */
    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return context.dataStore
    }

    /**
     * TokenManager 제공
     *
     * @param context Application Context
     * @return TokenManager 인스턴스
     */
    @Provides
    @Singleton
    fun provideTokenManager(
        @ApplicationContext context: Context
    ): TokenManager {
        return TokenManager(context)
    }
}
```

---

## Step 6: 공통 DTO

### 📁 파일: `ApiResponse.kt`
**위치**: `data/model/common/ApiResponse.kt`

```kotlin
package com.nextread.readpick.data.model.common

import kotlinx.serialization.Serializable

/**
 * 공통 API 응답 래퍼
 *
 * 백엔드가 일관된 응답 형식을 사용하는 경우에만 사용
 * 예시:
 * {
 *   "success": true,
 *   "data": { ... },
 *   "message": "Success"
 * }
 */
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
)
```

### 📁 파일: `ErrorResponse.kt`
**위치**: `data/model/common/ErrorResponse.kt`

```kotlin
package com.nextread.readpick.data.model.common

import kotlinx.serialization.Serializable

/**
 * 공통 에러 응답
 *
 * 백엔드 에러 응답 형식
 * 예시:
 * {
 *   "timestamp": "2025-10-30T12:00:00",
 *   "status": 404,
 *   "error": "Not Found",
 *   "message": "Book not found",
 *   "path": "/api/books/123"
 * }
 */
@Serializable
data class ErrorResponse(
    val timestamp: String,
    val status: Int,
    val error: String,
    val message: String,
    val path: String
)
```

---

## Step 7: 공통 UI 컴포넌트

### 📁 파일: `BookCard.kt`
**위치**: `presentation/common/component/BookCard.kt`

```kotlin
package com.nextread.readpick.presentation.common.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * 공통 책 카드 컴포넌트
 *
 * 모든 화면에서 재사용 가능한 책 표시 카드
 *
 * @param isbn13 책 ISBN
 * @param title 책 제목
 * @param author 저자
 * @param coverUrl 표지 이미지 URL
 * @param onClick 클릭 이벤트
 * @param modifier Modifier
 */
@Composable
fun BookCard(
    isbn13: String,
    title: String,
    author: String,
    coverUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(120.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // 책 표지
            AsyncImage(
                model = coverUrl,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            // 책 정보
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                // 제목
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 저자
                Text(
                    text = author,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
```

### 📁 파일: `LoadingIndicator.kt`
**위치**: `presentation/common/component/LoadingIndicator.kt`

```kotlin
package com.nextread.readpick.presentation.common.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * 공통 로딩 표시 컴포넌트
 *
 * API 호출 중일 때 표시
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
```

### 📁 파일: `ErrorMessage.kt`
**위치**: `presentation/common/component/ErrorMessage.kt`

```kotlin
package com.nextread.readpick.presentation.common.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * 공통 에러 메시지 컴포넌트
 *
 * API 호출 실패 시 표시
 *
 * @param message 에러 메시지
 * @param onRetry 재시도 버튼 클릭 이벤트
 */
@Composable
fun ErrorMessage(
    message: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.error
            )

            if (onRetry != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onRetry) {
                    Text("재시도")
                }
            }
        }
    }
}
```

### 📁 파일: `EmptyState.kt`
**위치**: `presentation/common/component/EmptyState.kt`

```kotlin
package com.nextread.readpick.presentation.common.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

/**
 * 공통 빈 상태 컴포넌트
 *
 * 데이터가 없을 때 표시
 *
 * @param message 표시할 메시지
 */
@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
```

---

## 🎯 완성 체크리스트

### Day 1-2: 팀장 작업
- [ ] Application 클래스 생성 (`@HiltAndroidApp`)
- [ ] AndroidManifest.xml 수정
- [ ] TokenManager 구현
- [ ] AuthInterceptor 구현
- [ ] LoggingInterceptor 구현
- [ ] NetworkModule 작성
- [ ] DataStoreModule 작성
- [ ] 공통 DTO 작성
- [ ] 공통 UI 컴포넌트 작성
- [ ] 빌드 성공 확인
- [ ] develop 브랜치에 푸시

### 테스트 방법
```kotlin
// MainActivity에서 테스트 (임시)
class MainActivity : ComponentActivity() {
    @Inject lateinit var tokenManager: TokenManager
    @Inject lateinit var bookApi: BookApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 테스트: 토큰 저장
        lifecycleScope.launch {
            tokenManager.saveToken("test-token-123")
            val token = tokenManager.getToken()
            Log.d("MainActivity", "Token: $token")  // "test-token-123" 출력되어야 함
        }

        // 테스트: API 호출
        lifecycleScope.launch {
            try {
                val books = bookApi.getBestsellers(null, 10)
                Log.d("MainActivity", "Books: $books")
            } catch (e: Exception) {
                Log.e("MainActivity", "Error: ${e.message}")
            }
        }
    }
}
```

---

## 🚨 주의사항

### 1. BASE_URL 변경 필수!
```kotlin
// NetworkModule.kt
private const val BASE_URL = "https://api.readpick.com/"  // TODO: 실제 서버 URL로 변경
```

### 2. 인터넷 권한 추가
**AndroidManifest.xml**:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### 3. API 인터페이스는 팀원들이 작성
```kotlin
// NetworkModule.kt
// TODO: 다른 API 인터페이스도 여기에 추가
@Provides
@Singleton
fun provideChatbotApi(retrofit: Retrofit): ChatbotApi {
    return retrofit.create(ChatbotApi::class.java)
}
```

---

## 📚 참고 자료

### Hilt 공식 문서
- https://developer.android.com/training/dependency-injection/hilt-android

### Retrofit 공식 문서
- https://square.github.io/retrofit/

### DataStore 공식 문서
- https://developer.android.com/topic/libraries/architecture/datastore

---

## 🎉 완성 후 팀원들에게

```
"공통 인프라 완성했습니다! 🎉

develop 브랜치 pull 받으세요.
이제 각자 작업 시작 가능합니다!

사용 방법:
1. @Inject로 자동 주입 받기
2. API 인터페이스만 정의하면 Retrofit이 자동 구현
3. 토큰은 TokenManager가 자동 관리
4. 공통 UI 컴포넌트 재사용

질문 있으면 언제든 물어보세요!"
```

---

**문서 버전**: 1.0
**최종 업데이트**: 2025-10-30
**작성자**: Claude Code
