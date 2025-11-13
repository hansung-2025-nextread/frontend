# 빠른 시작 가이드 ⚡

> 5분 안에 개발 시작하기!

## 🎯 나는 어떤 작업을 하나요?

**팀원1 (도서 관련):**
- 홈 화면 (`HomeScreen`)
- 도서 검색 (`SearchScreen`)
- 도서 상세 (`BookDetailScreen`)

**팀원2 (챗봇 + 리뷰):**
- 챗봇 화면 (`ChatbotScreen`)
- 리뷰 작성/목록 (`ReviewScreen`)

**팀원3 (마이페이지):**
- 마이페이지 (`MyPageScreen`)
- 컬렉션 관리 (`CollectionScreen`)

---

## 📋 체크리스트 (작업 시작 전)

```bash
✅ Android Studio 설치
✅ Java 17 설치 확인
✅ 프로젝트 clone 완료
✅ google-services.json 있음
✅ local.properties에 BASE_URL 설정
✅ 빌드 성공 (초록색 체크)
```

---

## 🚀 새 화면 만들기 (3단계)

### 1️⃣ Route 추가 (30초)

```kotlin
// presentation/navigation/Screen.kt

sealed class Screen(val route: String) {
    // ... 기존 코드 ...

    data object Home : Screen("home")  // ← 추가!
}
```

### 2️⃣ 화면 파일 만들기 (5분)

```
presentation/home/
├── HomeUiState.kt        (UI 상태)
├── HomeViewModel.kt      (로직)
└── HomeScreen.kt         (UI)
```

**복사해서 사용:**

```kotlin
// HomeUiState.kt
sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val data: List<String>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

// HomeViewModel.kt
@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
}

// HomeScreen.kt
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Text("홈 화면")
}
```

### 3️⃣ NavGraph에 연결 (1분)

```kotlin
// presentation/navigation/NavGraph.kt

composable(Screen.Home.route) {
    HomeScreen()
}
```

**끝! 이제 다른 화면에서 이동 가능:**

```kotlin
navController.navigate(Screen.Home.route)
```

---

## 🔌 API 연동하기 (5단계)

### 1️⃣ DTO 만들기

```kotlin
// data/model/book/BookDto.kt

@Serializable
data class BookDto(
    val isbn13: String,
    val title: String,
    val author: String
)
```

### 2️⃣ API 인터페이스

```kotlin
// data/remote/api/BookApi.kt

interface BookApi {
    @GET("v1/api/books/bestsellers")
    suspend fun getBestsellers(): ApiResponse<List<BookDto>>
}
```

### 3️⃣ Repository

```kotlin
// domain/repository/BookRepository.kt
interface BookRepository {
    suspend fun getBestsellers(): Result<List<BookDto>>
}

// data/repository/BookRepositoryImpl.kt
class BookRepositoryImpl @Inject constructor(
    private val bookApi: BookApi
) : BookRepository {
    override suspend fun getBestsellers() = runCatching {
        val response = bookApi.getBestsellers()
        if (response.success && response.data != null) {
            response.data
        } else {
            throw Exception(response.message ?: "에러")
        }
    }
}
```

### 4️⃣ DI 등록

```kotlin
// di/NetworkModule.kt
@Provides
@Singleton
fun provideBookApi(retrofit: Retrofit): BookApi {
    return retrofit.create(BookApi::class.java)
}

// di/RepositoryModule.kt
@Binds
@Singleton
abstract fun bindBookRepository(
    impl: BookRepositoryImpl
): BookRepository
```

### 5️⃣ ViewModel에서 사용

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    fun loadBooks() {
        viewModelScope.launch {
            bookRepository.getBestsellers()
                .onSuccess { books ->
                    _uiState.value = HomeUiState.Success(books)
                }
                .onFailure { exception ->
                    _uiState.value = HomeUiState.Error(exception.message ?: "에러")
                }
        }
    }
}
```

---

## 🎨 UI 만들기 (Compose 기본)

### 레이아웃

```kotlin
Column {                    // 세로로 배치
    Text("제목")
    Text("내용")
}

Row {                       // 가로로 배치
    Text("좌측")
    Text("우측")
}

Box {                       // 겹쳐서 배치
    Image(...)
    Text("위에 표시")
}
```

### 스크롤

```kotlin
LazyColumn {                // 세로 스크롤 (RecyclerView)
    items(books) { book ->
        BookCard(book)
    }
}

LazyRow {                   // 가로 스크롤
    items(categories) { category ->
        CategoryChip(category)
    }
}
```

### 버튼

```kotlin
Button(onClick = { /* 클릭 */ }) {
    Text("버튼")
}

IconButton(onClick = { /* 클릭 */ }) {
    Icon(Icons.Default.Search, "검색")
}
```

### 입력

```kotlin
var text by remember { mutableStateOf("") }

TextField(
    value = text,
    onValueChange = { text = it },
    label = { Text("제목") }
)
```

---

## 🔥 자주 쓰는 코드 스니펫

### ViewModel에서 API 호출

```kotlin
fun loadData() {
    viewModelScope.launch {
        _uiState.value = UiState.Loading

        repository.getData()
            .onSuccess { data ->
                _uiState.value = UiState.Success(data)
            }
            .onFailure { exception ->
                _uiState.value = UiState.Error(exception.message ?: "에러")
            }
    }
}
```

### Screen에서 상태 관찰

```kotlin
@Composable
fun MyScreen(viewModel: MyViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is UiState.Loading -> CircularProgressIndicator()
        is UiState.Success -> SuccessContent(state.data)
        is UiState.Error -> Text("에러: ${state.message}")
    }
}
```

### Navigation 이동

```kotlin
// 단순 이동
navController.navigate(Screen.Home.route)

// 파라미터 전달
navController.navigate("book/9788934972464")

// 뒤로가기
navController.popBackStack()

// 특정 화면까지 뒤로가기
navController.popBackStack(Screen.Home.route, inclusive = false)
```

---

## ⚠️ 주의사항

### ❌ 절대 하지 마세요

```kotlin
// MainActivity.kt 수정
// google-services.json 커밋
// 하드코딩 (BASE_URL, API 키 등)
```

### ✅ 반드시 하세요

```kotlin
// 작업 전 git pull
// 자주 커밋
// Log 찍어서 확인
// 에러는 try-catch
```

---

## 🆘 에러 해결 3단계

### 1단계: 에러 메시지 읽기

```
Logcat (Android Studio 하단) → 빨간색 에러 찾기
```

### 2단계: 구글 검색

```
"Android [에러 메시지]" 검색
StackOverflow 답변 참고
```

### 3단계: 팀장에게 문의

```
1. 에러 메시지 캡처
2. 작업 중인 파일 경로
3. 시도해본 해결 방법
```

---

## 📱 테스트하기

```bash
# 빌드
./gradlew assembleDebug

# 설치
./gradlew installDebug

# 에뮬레이터 실행
AVD Manager → Play 버튼
```

---

## 🎓 더 알아보기

자세한 내용은 **[TEAM_GUIDE.md](./TEAM_GUIDE.md)** 참고!

---

**화이팅! 🚀**
