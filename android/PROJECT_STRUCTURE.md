# ReadPick 안드로이드 프로젝트 구조

> Clean Architecture 기반 멀티 레이어 아키텍처

## 📁 프로젝트 구조

```
app/src/main/java/com/nextread/readpick/
│
├── 📦 data/                          # 데이터 레이어 (외부 데이터 소스)
│   ├── model/                        # DTO (Data Transfer Object)
│   │   ├── auth/                     # 인증 관련 DTO
│   │   ├── book/                     # 도서 관련 DTO
│   │   ├── user/                     # 사용자 관련 DTO
│   │   ├── chatbot/                  # 챗봇 관련 DTO
│   │   ├── review/                   # 리뷰 관련 DTO
│   │   └── recommend/                # 추천 관련 DTO
│   │
│   ├── remote/                       # 원격 데이터 소스
│   │   ├── api/                      # Retrofit API 인터페이스
│   │   └── interceptor/              # OkHttp 인터셉터 (JWT, 로깅)
│   │
│   ├── local/                        # 로컬 데이터 소스 (DataStore)
│   │
│   └── repository/                   # Repository 구현체
│
├── 🎯 domain/                        # 도메인 레이어 (비즈니스 로직)
│   ├── model/                        # 도메인 모델 (UI에서 사용)
│   ├── repository/                   # Repository 인터페이스
│   └── usecase/                      # Use Case (비즈니스 로직)
│
├── 🎨 presentation/                  # 프레젠테이션 레이어 (UI)
│   ├── auth/                         # 인증
│   │   └── login/                    # 로그인 화면
│   │
│   ├── home/                         # 홈 화면
│   │
│   ├── book/                         # 도서
│   │   ├── detail/                   # 도서 상세
│   │   └── list/                     # 도서 목록
│   │
│   ├── search/                       # 검색
│   │
│   ├── mypage/                       # 마이페이지
│   │
│   ├── chatbot/                      # 챗봇
│   │
│   ├── review/                       # 리뷰
│   │
│   ├── collection/                   # 컬렉션
│   │
│   └── common/                       # 공통 UI
│       ├── component/                # 재사용 가능한 컴포넌트
│       └── navigation/               # 네비게이션 설정
│
├── 💉 di/                            # Dependency Injection (Hilt 모듈)
│
└── ui/                               # 기존 UI 테마
    └── theme/

```

---

## 🏗️ 아키텍처 설명

### 3-Layer Architecture (Clean Architecture)

```
┌─────────────────────────────────────────────┐
│          Presentation Layer (UI)            │  ← 사용자가 보는 화면
│  • Screen (Composable)                      │
│  • ViewModel (상태 관리)                     │
│  • UiState (UI 상태)                        │
└─────────────────────────────────────────────┘
                    ↓ ↑
┌─────────────────────────────────────────────┐
│          Domain Layer (비즈니스 로직)         │  ← 앱의 핵심 로직
│  • Use Case (기능 단위)                      │
│  • Domain Model (순수 데이터)                │
│  • Repository Interface (인터페이스만)        │
└─────────────────────────────────────────────┘
                    ↓ ↑
┌─────────────────────────────────────────────┐
│          Data Layer (데이터 소스)            │  ← 외부 데이터
│  • Repository Implementation (구현체)        │
│  • API Service (Retrofit)                   │
│  • Local DataSource (DataStore)             │
│  • DTO (서버 데이터 형식)                     │
└─────────────────────────────────────────────┘
```

---

## 📦 각 패키지 상세 설명

### 1️⃣ data/ - 데이터 레이어

#### `data/model/` - DTO (Data Transfer Object)
**역할**: 백엔드 API 응답을 받는 데이터 클래스

**예시**:
```kotlin
// data/model/book/BookResponseDto.kt
@Serializable
data class BookResponseDto(
    val isbn13: String,
    val title: String,
    val author: String,
    val cover: String,
    val readingStatus: String?
)
```

**주의**: 이 DTO는 서버 응답 형식과 1:1 매칭됩니다.

---

#### `data/remote/api/` - Retrofit API 인터페이스
**역할**: 백엔드 API 호출 정의

**예시**:
```kotlin
// data/remote/api/BookApi.kt
interface BookApi {
    @GET("api/books/{isbn13}")
    suspend fun getBookDetail(
        @Path("isbn13") isbn: String
    ): BookResponseDto

    @GET("api/books/bestsellers")
    suspend fun getBestsellers(
        @Query("categoryId") categoryId: Long?,
        @Query("maxResults") maxResults: Int?
    ): List<BookDetailDto>
}
```

---

#### `data/remote/interceptor/` - OkHttp 인터셉터
**역할**:
- JWT 토큰을 자동으로 헤더에 추가
- 네트워크 요청/응답 로깅

**예시**:
```kotlin
// data/remote/interceptor/AuthInterceptor.kt
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.getToken()
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}
```

---

#### `data/local/` - 로컬 데이터 소스
**역할**: JWT 토큰, 사용자 설정 등을 DataStore에 저장/조회

**예시**:
```kotlin
// data/local/TokenManager.kt
class TokenManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[JWT_TOKEN_KEY] = token
        }
    }

    fun getToken(): String? = runBlocking {
        dataStore.data.first()[JWT_TOKEN_KEY]
    }
}
```

---

#### `data/repository/` - Repository 구현체
**역할**: Domain Layer의 Repository 인터페이스를 실제로 구현

**예시**:
```kotlin
// data/repository/BookRepositoryImpl.kt
class BookRepositoryImpl @Inject constructor(
    private val bookApi: BookApi
) : BookRepository {
    override suspend fun getBookDetail(isbn13: String): Result<Book> {
        return try {
            val dto = bookApi.getBookDetail(isbn13)
            Result.success(dto.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

---

### 2️⃣ domain/ - 도메인 레이어

#### `domain/model/` - 도메인 모델
**역할**: UI에서 실제로 사용하는 순수 데이터 모델 (DTO와 분리)

**예시**:
```kotlin
// domain/model/Book.kt
data class Book(
    val isbn13: String,
    val title: String,
    val author: String,
    val coverUrl: String,
    val readingStatus: ReadingStatus,
    val isSaved: Boolean
)

enum class ReadingStatus {
    NOT_STARTED, READING, COMPLETED, DROPPED
}
```

**왜 DTO와 분리?**
- 서버 응답이 변해도 UI 로직은 안전
- UI에 필요한 형태로 데이터 가공 가능

---

#### `domain/repository/` - Repository 인터페이스
**역할**: 데이터 소스에 접근하는 방법을 정의 (구현은 data layer)

**예시**:
```kotlin
// domain/repository/BookRepository.kt
interface BookRepository {
    suspend fun getBookDetail(isbn13: String): Result<Book>
    suspend fun getBestsellers(categoryId: Long?, maxResults: Int?): Result<List<Book>>
    suspend fun saveBook(isbn13: String): Result<Unit>
    suspend fun deleteBook(isbn13: String): Result<Unit>
}
```

---

#### `domain/usecase/` - Use Case
**역할**: 화면에서 필요한 비즈니스 로직을 캡슐화

**예시**:
```kotlin
// domain/usecase/GetBookDetailUseCase.kt
class GetBookDetailUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(isbn13: String): Result<Book> {
        return bookRepository.getBookDetail(isbn13)
    }
}
```

**언제 사용?**
- 여러 Repository를 조합해야 할 때
- 복잡한 비즈니스 로직이 있을 때
- 재사용 가능한 로직을 분리할 때

---

### 3️⃣ presentation/ - 프레젠테이션 레이어

#### 각 화면 패키지 구조
```
presentation/book/detail/
├── BookDetailScreen.kt       # Composable UI
├── BookDetailViewModel.kt    # 상태 관리
└── BookDetailUiState.kt      # UI 상태 정의
```

**예시**:
```kotlin
// presentation/book/detail/BookDetailUiState.kt
sealed interface BookDetailUiState {
    object Loading : BookDetailUiState
    data class Success(val book: Book) : BookDetailUiState
    data class Error(val message: String) : BookDetailUiState
}

// presentation/book/detail/BookDetailViewModel.kt
@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val getBookDetailUseCase: GetBookDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookDetailUiState>(BookDetailUiState.Loading)
    val uiState: StateFlow<BookDetailUiState> = _uiState.asStateFlow()

    fun loadBook(isbn13: String) {
        viewModelScope.launch {
            _uiState.value = BookDetailUiState.Loading
            getBookDetailUseCase(isbn13)
                .onSuccess { book ->
                    _uiState.value = BookDetailUiState.Success(book)
                }
                .onFailure { error ->
                    _uiState.value = BookDetailUiState.Error(error.message ?: "Unknown error")
                }
        }
    }
}

// presentation/book/detail/BookDetailScreen.kt
@Composable
fun BookDetailScreen(
    isbn13: String,
    viewModel: BookDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(isbn13) {
        viewModel.loadBook(isbn13)
    }

    when (val state = uiState) {
        is BookDetailUiState.Loading -> LoadingIndicator()
        is BookDetailUiState.Success -> BookContent(state.book)
        is BookDetailUiState.Error -> ErrorMessage(state.message)
    }
}
```

---

#### `presentation/common/component/` - 공통 컴포넌트
**역할**: 재사용 가능한 UI 컴포넌트

**예시**:
```kotlin
// presentation/common/component/BookCard.kt
@Composable
fun BookCard(
    book: Book,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() }
    ) {
        Column {
            AsyncImage(
                model = book.coverUrl,
                contentDescription = book.title
            )
            Text(text = book.title)
            Text(text = book.author)
        }
    }
}
```

---

#### `presentation/common/navigation/` - 네비게이션
**역할**: 화면 전환 라우팅

**예시**:
```kotlin
// presentation/common/navigation/NavGraph.kt
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(navController, startDestination) {
        composable("login") { LoginScreen() }
        composable("home") { HomeScreen() }
        composable("book/{isbn13}") { backStackEntry ->
            val isbn13 = backStackEntry.arguments?.getString("isbn13")
            BookDetailScreen(isbn13 = isbn13!!)
        }
    }
}

// 사용
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    data class BookDetail(val isbn13: String) : Screen("book/$isbn13")
}
```

---

### 4️⃣ di/ - Dependency Injection

**역할**: Hilt 모듈을 통해 의존성 주입 설정

**예시**:
```kotlin
// di/NetworkModule.kt
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.readpick.com/")
            .client(okHttpClient)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideBookApi(retrofit: Retrofit): BookApi {
        return retrofit.create(BookApi::class.java)
    }
}

// di/RepositoryModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBookRepository(
        bookRepositoryImpl: BookRepositoryImpl
    ): BookRepository
}
```

---

## 🔄 데이터 흐름 예시

### 사용자가 책 상세 화면을 열 때:

```
1. User 클릭
   ↓
2. BookDetailScreen (Composable)
   ↓
3. BookDetailViewModel.loadBook(isbn13)
   ↓
4. GetBookDetailUseCase(isbn13)
   ↓
5. BookRepository.getBookDetail(isbn13)
   ↓
6. BookRepositoryImpl → BookApi.getBookDetail(isbn13)
   ↓
7. Retrofit → OkHttp (AuthInterceptor가 JWT 추가)
   ↓
8. 백엔드 API 호출: GET /api/books/{isbn13}
   ↓
9. 응답: BookResponseDto (JSON)
   ↓
10. BookResponseDto → Book (도메인 모델로 변환)
   ↓
11. ViewModel → UiState 업데이트
   ↓
12. Screen 리컴포지션
   ↓
13. User가 화면에서 책 정보 확인
```

---

## 🎯 레이어별 책임

| 레이어 | 역할 | 의존성 방향 |
|--------|------|------------|
| **Presentation** | UI 표시, 사용자 입력 처리 | → Domain |
| **Domain** | 비즈니스 로직 (앱의 핵심) | 독립적 |
| **Data** | 외부 데이터 소스 (API, DB) | → Domain |

**의존성 규칙** (Dependency Rule):
- Presentation → Domain ← Data
- **절대 금지**: Domain → Data (도메인이 데이터를 직접 알면 안 됨)

---

## 📝 파일 명명 규칙

### DTO (Data Transfer Object)
- `BookResponseDto.kt`
- `LoginRequestDto.kt`
- `*Dto.kt` (서버 데이터 형식)

### Domain Model
- `Book.kt`
- `User.kt`
- `Review.kt` (순수 데이터 모델)

### Repository
- Interface: `BookRepository.kt` (domain/)
- Implementation: `BookRepositoryImpl.kt` (data/)

### Use Case
- `GetBookDetailUseCase.kt`
- `SaveBookUseCase.kt`
- `*UseCase.kt`

### ViewModel & Screen
- `BookDetailViewModel.kt`
- `BookDetailScreen.kt`
- `BookDetailUiState.kt`

### API Service
- `BookApi.kt`
- `AuthApi.kt`
- `*Api.kt`

---

## 🚀 다음 단계

1. ✅ 패키지 구조 생성 완료
2. ⏭️ 네트워크 레이어 구성 (Retrofit, OkHttp)
3. ⏭️ DTO 클래스 생성
4. ⏭️ API 인터페이스 정의
5. ⏭️ Repository 구현
6. ⏭️ ViewModel & Screen 구현

---

**문서 버전**: 1.0
**최종 업데이트**: 2025-10-30
**작성자**: Claude Code
