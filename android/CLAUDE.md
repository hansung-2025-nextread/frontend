# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ReadPick is an AI-powered book recommendation Android app built with Jetpack Compose and Clean Architecture. The app features Google OAuth login, personalized book recommendations, a chatbot interface, user collections, and a community platform.

## Build and Development Commands

### Building the App
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Install to connected device/emulator
./gradlew installDebug

# Clean build
./gradlew clean
```

### Running Tests
```bash
# Run all unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

### Viewing Signing Report
```bash
# Get SHA-1 for Firebase setup
./gradlew signingReport
```

## Architecture

This project uses **Clean Architecture** with three distinct layers:

### 1. Presentation Layer (`presentation/`)
- **Pattern**: MVVM with Jetpack Compose
- **Components**:
  - `Screen.kt`: Sealed class defining all navigation routes
  - `NavGraph.kt`: Navigation setup connecting all screens
  - `*Screen.kt`: Composable UI components
  - `*ViewModel.kt`: State management with `@HiltViewModel`
  - `*UiState.kt`: Sealed interfaces for UI states (Loading, Success, Error)
- **Key Rule**: ViewModels depend on domain layer repositories, never on data layer directly

### 2. Domain Layer (`domain/`)
- **Purpose**: Business logic and use cases
- **Components**:
  - `repository/`: Repository interfaces (not implementations)
  - `model/`: Domain models (pure data classes, UI-friendly)
  - `usecase/`: Business logic encapsulation
- **Key Rule**: Domain layer is independent - no Android or data dependencies

### 3. Data Layer (`data/`)
- **Purpose**: External data sources (API, local storage)
- **Components**:
  - `model/`: DTOs with `@Serializable` annotation
  - `remote/api/`: Retrofit API interfaces
  - `remote/interceptor/`: `AuthInterceptor` (JWT), `LoggingInterceptor`
  - `repository/`: Repository implementations (suffix: `Impl`)
  - `local/`: DataStore for token management
- **Key Rule**: Data layer implements domain interfaces

### Dependency Injection (`di/`)
- **Framework**: Hilt
- **Modules**:
  - `NetworkModule.kt`: Provides Retrofit, OkHttp, API interfaces
  - `RepositoryModule.kt`: Binds repository implementations
  - `DataStoreModule.kt`: Provides DataStore instance

## Navigation Architecture

The app uses Jetpack Navigation Compose with centralized route management:

### Critical Files (DO NOT modify without team consensus)
- `MainActivity.kt`: Already configured with NavHost, do not touch
- `presentation/navigation/Screen.kt`: Add new screen routes here
- `presentation/navigation/NavGraph.kt`: Connect screens to routes here

### Adding a New Screen
1. Add route to `Screen.kt`:
```kotlin
data object MyScreen : Screen("my_screen")
// For parameterized routes:
data object BookDetail : Screen("book/{isbn13}") {
    fun createRoute(isbn13: String) = "book/$isbn13"
}
```

2. Connect in `NavGraph.kt`:
```kotlin
composable(Screen.MyScreen.route) {
    MyScreen(
        onNavigate = { navController.navigate(...) }
    )
}
```

## API Integration Pattern

### Standard Flow for New API Endpoint

1. **Create DTO** (`data/model/**/`):
```kotlin
@Serializable
data class BookDto(
    val isbn13: String,
    val title: String,
    val author: String
)
```

2. **Define API Interface** (`data/remote/api/`):
```kotlin
interface BookApi {
    @GET("v1/api/books/bestsellers")
    suspend fun getBestsellers(): ApiResponse<List<BookDto>>
}
```

3. **Create Repository Interface** (`domain/repository/`):
```kotlin
interface BookRepository {
    suspend fun getBestsellers(): Result<List<BookDto>>
}
```

4. **Implement Repository** (`data/repository/`):
```kotlin
class BookRepositoryImpl @Inject constructor(
    private val bookApi: BookApi
) : BookRepository {
    override suspend fun getBestsellers() = runCatching {
        val response = bookApi.getBestsellers()
        if (response.success && response.data != null) {
            response.data
        } else {
            throw Exception(response.message ?: "Error")
        }
    }
}
```

5. **Register in DI**:
```kotlin
// NetworkModule.kt
@Provides @Singleton
fun provideBookApi(retrofit: Retrofit): BookApi =
    retrofit.create(BookApi::class.java)

// RepositoryModule.kt
@Binds @Singleton
abstract fun bindBookRepository(impl: BookRepositoryImpl): BookRepository
```

6. **Use in ViewModel**:
```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {
    fun loadBooks() {
        viewModelScope.launch {
            bookRepository.getBestsellers()
                .onSuccess { books -> /* update UI state */ }
                .onFailure { error -> /* handle error */ }
        }
    }
}
```

## Environment Configuration

### Required Files (Not in Git)
- `app/google-services.json`: Firebase configuration
- `local.properties`: Contains `BASE_URL` for backend API

### BASE_URL Setup
The app reads `BASE_URL` from `local.properties` and exposes it via `BuildConfig.BASE_URL`. This allows different developers to use different backend URLs (local, ngrok, production) without code changes.

## Authentication Flow

1. **Google OAuth** via Firebase Authentication
2. Backend validates `idToken` and returns JWT
3. `TokenManager` stores JWT in DataStore
4. `AuthInterceptor` adds JWT to all API requests automatically
5. On login success, checks `needsOnboarding` and `isAdmin` flags to route to appropriate screen

## Key Technical Decisions

### Why Clean Architecture?
- Separates UI, business logic, and data sources
- Makes testing easier (domain layer is pure Kotlin)
- Changes to API structure don't ripple through entire codebase

### Why Sealed Interfaces for UiState?
```kotlin
sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val data: List<Book>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
```
This pattern ensures exhaustive when-expressions and compile-time safety.

### Why Repository Pattern?
- Abstracts data sources (API, DB, cache)
- Domain layer doesn't know if data comes from network or local storage
- Easy to mock for testing

### Common Response Wrapper
All API responses use:
```kotlin
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
)
```

## Backend API Integration

The backend API base URL is configured in `local.properties`. See `BACKEND_API_REFERENCE.md` for comprehensive API documentation.

### Key API Patterns
- Authentication: `POST /auth/{provider}` returns JWT
- Most endpoints require `Authorization: Bearer {JWT}` header
- Book operations: `/v1/api/books/*`
- User operations: `/v1/api/users/me/*`
- Community: `/v1/api/community/*`
- Admin: `/v1/api/admin/*`

## Critical Constraints

### DO NOT Modify
- `MainActivity.kt`: Navigation and theme already configured
- `ReadPickApplication.kt`: Hilt application setup
- `build.gradle.kts`: Dependency versions managed by team lead
- `google-services.json`: Firebase configuration (unique per developer)

### Team Collaboration Rules
- Each developer has their own SHA-1 certificate registered in Firebase
- Each developer configures their own `BASE_URL` in `local.properties`
- Never commit `google-services.json` or `local.properties`
- Use feature branches, never commit directly to `main`
- Screen navigation changes require team discussion (affects everyone)

## Feature Modules by Team Member

### Team Lead (Complete)
- Login (`presentation/auth/login/`)
- Onboarding (`presentation/onboarding/`)
- Admin Dashboard (`presentation/admin/`)

### Team Member 1 (Books)
- Home Screen (`presentation/home/`)
- Search (`presentation/search/`)
- Book Detail (`presentation/book/`)

### Team Member 2 (Engagement)
- Chatbot (`presentation/chatbot/`)
- Reviews (`presentation/review/`)

### Team Member 3 (User Profile)
- My Page (`presentation/mypage/`)
- Collections (`presentation/collection/`)

### Shared
- Community (`presentation/community/`)

## Logging and Debugging

### Log Tag Pattern
```kotlin
companion object {
    private const val TAG = "FeatureName"
}

Log.d(TAG, "Debug message")
Log.e(TAG, "Error message", exception)
```

### Viewing Logs
Use Android Studio Logcat with filter: `tag:YourTagName`

## Common Pitfalls

1. **Don't use blocking calls in ViewModels**: Always wrap API calls in `viewModelScope.launch`
2. **Don't reference Activity/Context in ViewModels**: Use Hilt for dependencies
3. **Don't modify MainActivity**: Navigation is already set up centrally
4. **Don't hardcode API URLs**: Use `BuildConfig.BASE_URL`
5. **Don't skip error handling**: Always handle both success and failure cases in repositories
6. **Don't create new navigation patterns**: Use the established `Screen` + `NavGraph` pattern

## Testing Strategy

When writing tests:
- **Unit tests** for ViewModels: Mock repositories
- **Repository tests**: Mock API interfaces
- **UI tests**: Use Compose testing framework

## Git Workflow

```bash
# Start new feature
git checkout -b feature/feature-name

# Regular commits
git add .
git commit -m "feat: description"

# Before pushing, pull latest
git pull origin dev

# Push feature branch
git push origin feature/feature-name
```

## Version Information

- **Minimum SDK**: 26 (Android 8.0)
- **Target SDK**: 36
- **Compile SDK**: 36
- **Kotlin**: 2.0+
- **Compose**: BOM-based versioning

## Additional Documentation

For more details, refer to:
- `README.md`: Setup instructions
- `PROJECT_STRUCTURE.md`: Detailed architecture explanation
- `TEAM_GUIDE.md`: Developer onboarding guide
- `QUICKSTART.md`: Quick reference for common tasks
- `BACKEND_API_REFERENCE.md`: Complete API documentation