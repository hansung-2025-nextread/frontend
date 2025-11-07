# 팀원 개발 가이드 📱

> NextRead Android 앱 개발을 위한 초보자 친화적 가이드
> 안드로이드 스튜디오와 Kotlin이 처음이어도 따라할 수 있습니다!

**작성일**: 2025-11-07
**버전**: 1.0
**작성자**: 팀장 (온보딩 기능 구현 완료)

---

## 📋 목차

1. [시작하기 전에](#시작하기-전에)
2. [프로젝트 구조 이해하기](#프로젝트-구조-이해하기)
3. [Navigation 이해하기](#navigation-이해하기)
4. [새로운 화면 추가하는 방법](#새로운-화면-추가하는-방법)
5. [API 연동하는 방법](#api-연동하는-방법)
6. [자주 묻는 질문 (FAQ)](#자주-묻는-질문-faq)
7. [트러블슈팅](#트러블슈팅)

---

## 시작하기 전에

### ✅ 필수 준비물

1. **Android Studio 설치** (최신 버전)
   - 다운로드: https://developer.android.com/studio
   - 설치 시 모든 기본 옵션 선택

2. **Java 17 설치**
   - 이미 `local.properties`에 설정되어 있음
   - 확인 방법: Terminal에서 `java -version`

3. **필수 파일 확인**
   - `app/google-services.json` (Firebase 설정)
   - `local.properties` (BASE_URL 설정)

### 📂 프로젝트 열기

```bash
# 1. Git clone (이미 했다면 생략)
git clone [repository-url]
cd readpick/android

# 2. Android Studio에서 열기
# File → Open → android 폴더 선택

# 3. Gradle Sync 대기 (처음엔 시간이 좀 걸림)
# 자동으로 진행됨. 하단에 "Gradle Sync" 진행 상황 표시
```

### ▶️ 앱 실행하기

```bash
# 방법 1: Android Studio 상단의 초록색 재생 버튼 클릭

# 방법 2: Terminal에서
./gradlew installDebug

# 에뮬레이터 설정 방법
# Tools → Device Manager → Create Device → Pixel 5 (API 34) 선택
```

---

## 프로젝트 구조 이해하기

### 📁 핵심 디렉토리 구조

```
app/src/main/java/com/nextread/readpick/
│
├── MainActivity.kt                    ⚠️ 건드리지 마세요!
│
├── presentation/                      ✅ UI 작업은 여기서!
│   ├── navigation/                    ⚠️ 중요! 여기만 수정
│   │   ├── Screen.kt                  → 화면 route 추가
│   │   └── NavGraph.kt                → 화면 연결
│   │
│   ├── auth/login/                    (팀장 완료)
│   ├── onboarding/                    (팀장 완료)
│   │
│   ├── home/                          ← 팀원1: 여기 작업
│   ├── search/                        ← 팀원1
│   ├── book/                          ← 팀원1
│   │
│   ├── chatbot/                       ← 팀원2: 여기 작업
│   ├── review/                        ← 팀원2
│   │
│   ├── mypage/                        ← 팀원3: 여기 작업
│   └── collection/                    ← 팀원3
│
├── domain/repository/                 ✅ API 인터페이스 정의
│   ├── AuthRepository.kt              (팀장 완료)
│   ├── OnboardingRepository.kt        (팀장 완료)
│   └── BookRepository.kt              ← 팀원1: 여기 추가
│
├── data/
│   ├── model/                         ✅ DTO (데이터 클래스)
│   │   ├── auth/
│   │   ├── onboarding/
│   │   └── book/                      ← 팀원1: 여기 추가
│   │
│   ├── remote/api/                    ✅ Retrofit API
│   │   ├── AuthApi.kt
│   │   ├── OnboardingApi.kt
│   │   └── BookApi.kt                 ← 팀원1: 여기 추가
│   │
│   └── repository/                    ✅ Repository 구현
│       ├── AuthRepositoryImpl.kt
│       ├── OnboardingRepositoryImpl.kt
│       └── BookRepositoryImpl.kt      ← 팀원1: 여기 추가
│
└── di/                                ✅ 의존성 주입 설정
    ├── NetworkModule.kt               → API 추가
    └── RepositoryModule.kt            → Repository 바인딩
```

### 🚫 절대 건드리면 안 되는 파일

```
❌ MainActivity.kt              → 팀장이 이미 설정 완료
❌ ReadPickApplication.kt       → Hilt 설정
❌ build.gradle.kts             → 의존성 관리 (팀장만)
❌ google-services.json         → Firebase 설정
❌ local.properties             → 로컬 환경 설정
```

### ✅ 수정해도 되는 파일

```
✅ presentation/navigation/Screen.kt      → 화면 route 추가
✅ presentation/navigation/NavGraph.kt    → 화면 연결
✅ di/NetworkModule.kt                    → API 추가
✅ di/RepositoryModule.kt                 → Repository 바인딩
✅ 본인이 작업하는 feature/ 디렉토리     → 자유롭게 작업
```

---

## Navigation 이해하기

### 🧭 Navigation이란?

안드로이드 앱에서 **화면과 화면을 이동**하는 기능입니다.
우리 프로젝트는 **Jetpack Navigation Compose**를 사용합니다.

#### 예시: 카카오톡 앱
```
로그인 화면 → 친구 목록 → 채팅방 → 프로필
```

이렇게 화면을 이동하는 것이 Navigation입니다!

### 🔑 핵심 개념

#### 1. **Route** (경로)

화면마다 고유한 이름(주소)이 있습니다.

```kotlin
// presentation/navigation/Screen.kt

sealed class Screen(val route: String) {
    data object Login : Screen("login")           // 로그인 화면 주소
    data object Onboarding : Screen("onboarding") // 온보딩 화면 주소
    data object Home : Screen("home")             // 홈 화면 주소

    // TODO: 팀원들이 여기에 추가!
    // data object BookDetail : Screen("book/{isbn13}")
}
```

#### 2. **NavGraph** (화면 연결 지도)

모든 화면을 연결하는 설정 파일입니다.

```kotlin
// presentation/navigation/NavGraph.kt

@Composable
fun ReadPickNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination  // 시작 화면
    ) {
        // 로그인 화면 (팀장 완료)
        composable(Screen.Login.route) {
            LoginScreen(...)
        }

        // TODO: 팀원들이 여기에 화면 추가!
        // composable(Screen.Home.route) {
        //     HomeScreen(...)
        // }
    }
}
```

#### 3. **NavController** (네비게이터)

화면 이동을 실제로 수행하는 객체입니다.

```kotlin
// 사용 예시
navController.navigate(Screen.Home.route)  // Home 화면으로 이동
navController.popBackStack()               // 뒤로가기
```

### 🎯 MainActivity는 왜 건드리면 안 되나요?

```kotlin
// MainActivity.kt (팀장이 이미 설정 완료)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NextReadTheme {
                val navController = rememberNavController()

                // 이미 NavGraph가 설정되어 있음!
                ReadPickNavGraph(navController = navController)
            }
        }
    }
}
```

**이유:**
- ✅ 이미 Navigation 설정이 완료되어 있음
- ✅ 새 화면 추가는 `NavGraph.kt`에서만 하면 됨
- ✅ MainActivity 수정 시 전체 앱이 깨질 수 있음
- ✅ Git Conflict 발생 가능성 높음

---

## 새로운 화면 추가하는 방법

### 📝 단계별 가이드 (팀원1 - HomeScreen 예시)

#### Step 1: Screen Route 추가

```kotlin
// presentation/navigation/Screen.kt

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")

    // ✅ 추가!
    data object BookDetail : Screen("book/{isbn13}") {
        fun createRoute(isbn13: String) = "book/$isbn13"
    }
    data object Search : Screen("search")
}
```

#### Step 2: UI State 만들기

```kotlin
// presentation/home/HomeUiState.kt

/**
 * 홈 화면 UI 상태
 */
sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val bestsellers: List<Book>,
        val recommendations: List<Book>
    ) : HomeUiState

    data class Error(val message: String) : HomeUiState
}
```

#### Step 3: ViewModel 만들기

```kotlin
// presentation/home/HomeViewModel.kt

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadBooks()
    }

    fun loadBooks() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            bookRepository.getBestsellers()
                .onSuccess { books ->
                    _uiState.value = HomeUiState.Success(
                        bestsellers = books,
                        recommendations = emptyList()
                    )
                }
                .onFailure { exception ->
                    _uiState.value = HomeUiState.Error(
                        message = exception.message ?: "에러 발생"
                    )
                }
        }
    }
}
```

#### Step 4: Composable Screen 만들기

```kotlin
// presentation/home/HomeScreen.kt

@Composable
fun HomeScreen(
    onBookClick: (String) -> Unit,  // isbn13을 받아서 상세 화면으로
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold { padding ->
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                CircularProgressIndicator()
            }

            is HomeUiState.Success -> {
                LazyColumn(modifier = Modifier.padding(padding)) {
                    items(state.bestsellers) { book ->
                        BookCard(
                            book = book,
                            onClick = { onBookClick(book.isbn13) }
                        )
                    }
                }
            }

            is HomeUiState.Error -> {
                Text("에러: ${state.message}")
            }
        }
    }
}
```

#### Step 5: NavGraph에 화면 연결

```kotlin
// presentation/navigation/NavGraph.kt

@Composable
fun ReadPickNavGraph(...) {
    NavHost(...) {
        // ... 기존 화면들 ...

        // ✅ HomeScreen 추가
        composable(Screen.Home.route) {
            HomeScreen(
                onBookClick = { isbn13 ->
                    navController.navigate(
                        Screen.BookDetail.createRoute(isbn13)
                    )
                }
            )
        }

        // ✅ BookDetailScreen 추가
        composable(
            route = Screen.BookDetail.route,
            arguments = listOf(
                navArgument("isbn13") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val isbn13 = backStackEntry.arguments?.getString("isbn13") ?: ""
            BookDetailScreen(
                isbn13 = isbn13,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
```

#### Step 6: 완료! 🎉

이제 다른 화면에서 이렇게 이동할 수 있습니다:

```kotlin
navController.navigate(Screen.Home.route)
```

---

## API 연동하는 방법

### 🔌 단계별 가이드 (팀원1 - BookApi 예시)

#### Step 1: DTO (데이터 클래스) 만들기

```kotlin
// data/model/book/BookDto.kt

import kotlinx.serialization.Serializable

@Serializable
data class BookDto(
    val isbn13: String,
    val title: String,
    val author: String,
    val cover: String,        // 표지 이미지 URL
    val description: String,
    val categoryName: String
)

// data/model/book/BookListResponse.kt

@Serializable
data class BookListResponse(
    val books: List<BookDto>,
    val totalCount: Int
)
```

#### Step 2: Retrofit API 인터페이스 만들기

```kotlin
// data/remote/api/BookApi.kt

import retrofit2.http.*

interface BookApi {

    /**
     * 베스트셀러 목록 조회
     */
    @GET("v1/api/books/bestsellers")
    suspend fun getBestsellers(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): ApiResponse<BookListResponse>

    /**
     * 도서 상세 조회
     */
    @GET("v1/api/books/{isbn13}")
    suspend fun getBookDetail(
        @Path("isbn13") isbn13: String
    ): ApiResponse<BookDto>

    /**
     * 내 서재에 책 저장
     */
    @POST("v1/api/books/{isbn13}")
    suspend fun saveBook(
        @Path("isbn13") isbn13: String
    ): ApiResponse<Unit>
}
```

**참고:** `ApiResponse`는 이미 만들어져 있습니다!

```kotlin
// data/model/common/ApiResponse.kt (이미 존재)

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
)
```

#### Step 3: Repository 인터페이스 만들기

```kotlin
// domain/repository/BookRepository.kt

interface BookRepository {

    /**
     * 베스트셀러 목록 조회
     */
    suspend fun getBestsellers(): Result<List<BookDto>>

    /**
     * 도서 상세 조회
     */
    suspend fun getBookDetail(isbn13: String): Result<BookDto>

    /**
     * 내 서재에 저장
     */
    suspend fun saveBook(isbn13: String): Result<Unit>
}
```

#### Step 4: Repository 구현체 만들기

```kotlin
// data/repository/BookRepositoryImpl.kt

import android.util.Log
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val bookApi: BookApi
) : BookRepository {

    override suspend fun getBestsellers(): Result<List<BookDto>> = runCatching {
        Log.d(TAG, "베스트셀러 조회 API 호출")

        val response = bookApi.getBestsellers()

        if (response.success && response.data != null) {
            Log.d(TAG, "베스트셀러 ${response.data.books.size}개 조회 성공")
            response.data.books
        } else {
            Log.e(TAG, "베스트셀러 조회 실패: ${response.message}")
            throw Exception(response.message ?: "베스트셀러를 불러올 수 없습니다")
        }
    }.onFailure { exception ->
        Log.e(TAG, "베스트셀러 조회 에러", exception)
    }

    override suspend fun getBookDetail(isbn13: String): Result<BookDto> = runCatching {
        Log.d(TAG, "도서 상세 조회 API 호출: $isbn13")

        val response = bookApi.getBookDetail(isbn13)

        if (response.success && response.data != null) {
            Log.d(TAG, "도서 상세 조회 성공: ${response.data.title}")
            response.data
        } else {
            throw Exception(response.message ?: "도서 정보를 불러올 수 없습니다")
        }
    }.onFailure { exception ->
        Log.e(TAG, "도서 상세 조회 에러", exception)
    }

    override suspend fun saveBook(isbn13: String): Result<Unit> = runCatching {
        Log.d(TAG, "책 저장 API 호출: $isbn13")

        val response = bookApi.saveBook(isbn13)

        if (response.success) {
            Log.d(TAG, "책 저장 성공")
            Unit
        } else {
            throw Exception(response.message ?: "책을 저장할 수 없습니다")
        }
    }.onFailure { exception ->
        Log.e(TAG, "책 저장 에러", exception)
    }

    companion object {
        private const val TAG = "BookRepository"
    }
}
```

#### Step 5: DI 모듈에 등록

```kotlin
// di/NetworkModule.kt

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // ... 기존 코드 ...

    /**
     * BookApi 제공 - 추가!
     */
    @Provides
    @Singleton
    fun provideBookApi(retrofit: Retrofit): BookApi {
        return retrofit.create(BookApi::class.java)
    }
}
```

```kotlin
// di/RepositoryModule.kt

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    // ... 기존 코드 ...

    /**
     * BookRepository 바인딩 - 추가!
     */
    @Binds
    @Singleton
    abstract fun bindBookRepository(
        bookRepositoryImpl: BookRepositoryImpl
    ): BookRepository
}
```

#### Step 6: ViewModel에서 사용하기

```kotlin
// presentation/home/HomeViewModel.kt

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bookRepository: BookRepository  // Hilt가 자동으로 주입!
) : ViewModel() {

    fun loadBooks() {
        viewModelScope.launch {
            bookRepository.getBestsellers()
                .onSuccess { books ->
                    // 성공 처리
                }
                .onFailure { exception ->
                    // 에러 처리
                }
        }
    }
}
```

---

## 자주 묻는 질문 (FAQ)

### Q1: Kotlin 기초 문법을 어디서 배우나요?

**A:** 아래 간단한 예제만 알면 충분합니다!

```kotlin
// 1. 변수 선언
val name = "홍길동"        // 변경 불가능 (final)
var age = 25              // 변경 가능

// 2. 함수 정의
fun greet(name: String): String {
    return "안녕하세요, $name님!"
}

// 3. 데이터 클래스
data class Book(
    val title: String,
    val author: String
)

// 4. Null 처리
val book: Book? = null    // Nullable
book?.title               // Safe call

// 5. Lambda
books.map { it.title }    // it = 현재 아이템
```

### Q2: `@Composable`이 뭔가요?

**A:** UI를 만드는 함수라는 표시입니다.

```kotlin
@Composable
fun MyScreen() {
    Column {
        Text("제목")
        Button(onClick = { /* 클릭 */ }) {
            Text("버튼")
        }
    }
}
```

### Q3: `viewModelScope.launch`가 뭔가요?

**A:** 백그라운드에서 작업(API 호출 등)을 하는 코드입니다.

```kotlin
viewModelScope.launch {
    // 여기서 API 호출
    val result = bookRepository.getBestsellers()
}
```

### Q4: `hiltViewModel()`이 뭔가요?

**A:** ViewModel을 자동으로 만들어주는 마법입니다.

```kotlin
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()  // 자동으로 생성됨!
) {
    // viewModel 사용
}
```

### Q5: 로그(Log)는 어떻게 확인하나요?

**A:** Android Studio 하단의 **Logcat** 탭에서 확인

```kotlin
Log.d("MyTag", "디버그 로그")
Log.e("MyTag", "에러 로그")
```

필터 설정: `tag:MyTag` 입력하면 해당 태그만 표시

### Q6: 빌드 에러가 발생하면?

**A:** 순서대로 시도

```bash
# 1. Clean Project
Build → Clean Project

# 2. Rebuild Project
Build → Rebuild Project

# 3. Invalidate Caches
File → Invalidate Caches → Invalidate and Restart

# 4. Gradle Sync
File → Sync Project with Gradle Files
```

### Q7: Git Conflict 발생 시?

**A:** 팀장에게 바로 연락!

```bash
# 작업 전 항상 최신 코드 받기
git checkout develop
git pull origin develop

# 작업 후 충돌 방지
git add .
git commit -m "feat: 기능 설명"
git pull origin develop  # 최신 코드 반영
git push origin feature/my-branch
```

---

## 트러블슈팅

### ❌ 에러: "Unresolved reference: hiltViewModel"

**원인:** import 누락

**해결:**
```kotlin
import androidx.hilt.navigation.compose.hiltViewModel
```

### ❌ 에러: "lateinit property has not been initialized"

**원인:** Repository가 주입되지 않음

**해결:** DI 모듈 설정 확인
```kotlin
// di/NetworkModule.kt에 API 추가했는지 확인
// di/RepositoryModule.kt에 Repository 바인딩했는지 확인
```

### ❌ 에러: "Cannot access database on the main thread"

**원인:** 메인 스레드에서 DB/API 호출

**해결:** `viewModelScope.launch` 사용
```kotlin
viewModelScope.launch {
    // 여기서 API 호출
}
```

### ❌ 화면이 하얗게 나옴

**원인:** Composable 함수 호출 누락

**해결:** Preview 추가
```kotlin
@Preview
@Composable
fun PreviewMyScreen() {
    MyScreen()
}
```

### ❌ 에러: "401 Unauthorized"

**원인:** JWT 토큰 만료 또는 없음

**해결:**
1. 로그아웃 후 다시 로그인
2. `TokenManager`에 토큰이 저장되었는지 확인
3. `AuthInterceptor`가 토큰을 헤더에 추가하는지 확인

---

## 📚 추가 학습 자료

### 공식 문서
- [Jetpack Compose 기초](https://developer.android.com/jetpack/compose/tutorial)
- [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- [Kotlin 기초](https://kotlinlang.org/docs/basic-syntax.html)

### 팀 내부 문서
- `TEAM_ROLES.md` - 역할 분담
- `onboarding_guide.md` (backend) - API 명세

### 유용한 단축키

```
Ctrl + Space          자동완성
Ctrl + Shift + O      import 자동 추가
Ctrl + Alt + L        코드 포맷팅
Shift + F10           앱 실행
Ctrl + F9             빌드
```

---

## 💬 도움이 필요할 때

1. **에러 메시지 읽기**: Logcat에서 빨간색 에러 확인
2. **구글 검색**: "Android [에러 메시지]" 검색
3. **팀장에게 문의**:
   - 화면 캡처 (에러 메시지 포함)
   - 작업 중인 파일 경로
   - 시도해본 해결 방법


---

## ✅ 작업 체크리스트

새 화면 추가 시 확인할 것:

- [ ] `Screen.kt`에 route 추가
- [ ] DTO 클래스 생성 (`data/model/`)
- [ ] API 인터페이스 생성 (`data/remote/api/`)
- [ ] Repository 인터페이스 생성 (`domain/repository/`)
- [ ] Repository 구현체 생성 (`data/repository/`)
- [ ] `NetworkModule.kt`에 API 추가
- [ ] `RepositoryModule.kt`에 Repository 바인딩
- [ ] UiState 클래스 생성
- [ ] ViewModel 생성 (`@HiltViewModel`)
- [ ] Screen Composable 생성
- [ ] `NavGraph.kt`에 화면 연결
- [ ] 빌드 성공 확인
- [ ] 실제 기기에서 테스트
- [ ] Git commit & push

---

**끝! 궁금한 점은 언제든 팀장에게 물어보세요! 🚀**
