# ReadPick 백엔드 API 레퍼런스

> 안드로이드 앱 개발을 위한 백엔드 API 문서

## 목차
- [인증 (Authentication)](#인증-authentication)
- [도서 도메인 (Book Domain)](#도서-도메인-book-domain)
- [사용자 도메인 (User Domain)](#사용자-도메인-user-domain)
- [챗봇 도메인 (Chatbot Domain)](#챗봇-도메인-chatbot-domain)
- [리뷰 도메인 (Review Domain)](#리뷰-도메인-review-domain)
- [추천 도메인 (Recommend Domain)](#추천-도메인-recommend-domain)
- [DTO 구조](#dto-구조)
- [주요 엔티티](#주요-엔티티)

---

## 인증 (Authentication)

### AuthController (`/auth`)

#### OAuth 로그인
```
POST /auth/{provider}
```
- **Provider**: google, kakao, naver
- **Request Body**:
  ```json
  {
    "idToken": "string"
  }
  ```
- **Response**:
  ```json
  {
    "jwt": "string",
    "email": "string",
    "name": "string",
    "picture": "string"
  }
  ```

---

## 도서 도메인 (Book Domain)

### BookController (`/api/books`)

#### 1. 베스트셀러 조회
```
GET /api/books/bestsellers?categoryId={id}&maxResults={num}
```
- **Query Parameters**:
  - `categoryId`: 카테고리 ID (optional)
  - `maxResults`: 결과 개수 (optional, default: 10)
- **Response**: `List<BookDetailDto>`
- **참고**: 사용자 선호도 자동 업데이트됨

#### 2. 독서 상태 업데이트
```
PUT /api/books/saved/{isbn13}/status
```
- **Path Variable**: `isbn13`
- **Request Body**:
  ```json
  {
    "status": "NOT_STARTED|READING|COMPLETED|DROPPED"
  }
  ```

### AladinApiController (`/api/books`)

#### 3. 도서 상세 조회
```
GET /api/books/{isbn13}
```
- **Path Variable**: `isbn13`
- **Response**: `BookDetailDto`
- **참고**: Aladin API 연동

#### 4. 내 서재에 책 저장
```
POST /api/books/{isbn13}
```
- **Path Variable**: `isbn13`
- **Response**: 저장된 책 정보

#### 5. 내 서재에서 책 삭제
```
DELETE /api/books/{isbn13}
```
- **Path Variable**: `isbn13`

### SimilarBookSearchController (`/api/books/search-similar`)

#### 6. RAG 기반 유사 도서 검색
```
POST /api/books/search-similar
```
- **Request Body**:
  ```json
  {
    "query": "string",
    "topK": 10
  }
  ```
- **Response**:
  ```json
  {
    "query": "string",
    "books": [
      {
        "isbn13": "string",
        "title": "string",
        "author": "string",
        "similarity": 0.95
      }
    ]
  }
  ```

### BookEmbeddingController (Admin: `/api/admin/book-embeddings`)

#### 7. 도서 임베딩 생성 (관리자)
```
POST /api/admin/book-embeddings/generate
```
- **참고**: 약 30-60분 소요

#### 8. 임베딩 통계 조회 (관리자)
```
GET /api/admin/book-embeddings/stats
```

---

## 사용자 도메인 (User Domain)

### UserController (`/api/users/me`)

#### 1. 내 서재 조회
```
GET /api/users/me/saved-books?page={num}&size={num}
```
- **Query Parameters**:
  - `page`: 페이지 번호 (0부터 시작)
  - `size`: 페이지 크기
- **Response**: `Page<BookResponseDto>`

### UserCollectionController (`/api/users/me/collections`)

#### 2. 컬렉션 생성
```
POST /api/users/me/collections
```
- **Request Body**:
  ```json
  {
    "name": "string"
  }
  ```
- **Response**:
  ```json
  {
    "id": 1,
    "name": "string",
    "bookCount": 0,
    "createdAt": "2025-10-30T12:00:00"
  }
  ```

#### 3. 내 컬렉션 목록 조회
```
GET /api/users/me/collections
```
- **Response**: `List<UserCollectionResponse>`

#### 4. 컬렉션 이름 수정
```
PUT /api/users/me/collections/{collectionId}
```
- **Path Variable**: `collectionId`
- **Request Body**:
  ```json
  {
    "name": "string"
  }
  ```

#### 5. 컬렉션 삭제
```
DELETE /api/users/me/collections/{collectionId}
```
- **Path Variable**: `collectionId`

#### 6. 컬렉션에 책 추가
```
POST /api/users/me/collections/{collectionId}/books/{isbn13}
```
- **Path Variables**: `collectionId`, `isbn13`

#### 7. 컬렉션에서 책 제거
```
DELETE /api/users/me/collections/{collectionId}/books/{isbn13}
```
- **Path Variables**: `collectionId`, `isbn13`

#### 8. 컬렉션 내 책 목록 조회
```
GET /api/users/me/collections/{collectionId}/books?page={num}&size={num}
```
- **Path Variable**: `collectionId`
- **Query Parameters**: `page`, `size`
- **Response**: `Page<CollectionBookResponse>`

---

## 챗봇 도메인 (Chatbot Domain)

### ChatbotController (`/api/chatbot/search`)

#### 1. 챗봇 검색
```
POST /api/chatbot/search
```
- **Request Body**:
  ```json
  {
    "query": "string"
  }
  ```
- **Response**:
  ```json
  {
    "message": "string",
    "books": []
  }
  ```
- **참고**: 작가/가격/베스트셀러/RAG 통합 검색

### ChatbotConversationController (`/api/chatbot/conversations`)

#### 2. 새 대화 세션 생성
```
POST /api/chatbot/conversations/sessions
```
- **Response**:
  ```json
  {
    "id": 1,
    "title": "새 대화",
    "createdAt": "2025-10-30T12:00:00",
    "updatedAt": "2025-10-30T12:00:00"
  }
  ```

#### 3. 대화 세션 목록 조회
```
GET /api/chatbot/conversations/sessions
```
- **Response**: `List<ChatSessionSummaryDto>` (최신순)

#### 4. 대화 세션 상세 조회
```
GET /api/chatbot/conversations/sessions/{sessionId}
```
- **Path Variable**: `sessionId`
- **Response**:
  ```json
  {
    "id": 1,
    "title": "string",
    "messages": [
      {
        "role": "USER|ASSISTANT",
        "content": "string",
        "createdAt": "2025-10-30T12:00:00"
      }
    ],
    "createdAt": "2025-10-30T12:00:00"
  }
  ```

#### 5. 메시지 전송
```
POST /api/chatbot/conversations/sessions/{sessionId}/messages
```
- **Path Variable**: `sessionId`
- **Request Body**:
  ```json
  {
    "message": "string"
  }
  ```
- **Response**:
  ```json
  {
    "sessionId": 1,
    "reply": "string",
    "books": []
  }
  ```

#### 6. 대화 세션 삭제
```
DELETE /api/chatbot/conversations/sessions/{sessionId}
```
- **Path Variable**: `sessionId`

---

## 리뷰 도메인 (Review Domain)

### ReviewController (`/api`)

#### 1. 리뷰 작성
```
POST /api/books/{isbn13}/reviews
```
- **Path Variable**: `isbn13`
- **Request Body**:
  ```json
  {
    "content": "string"
  }
  ```
- **Response**:
  ```json
  {
    "id": 1,
    "userName": "string",
    "userPicture": "string",
    "content": "string",
    "createdAt": "2025-10-30T12:00:00",
    "updatedAt": "2025-10-30T12:00:00"
  }
  ```
- **참고**: 내 서재에 있는 책만 리뷰 작성 가능

#### 2. 내 리뷰 수정
```
PUT /api/books/{isbn13}/reviews
```
- **Path Variable**: `isbn13`
- **Request Body**:
  ```json
  {
    "content": "string"
  }
  ```

#### 3. 내 리뷰 삭제
```
DELETE /api/books/{isbn13}/reviews
```
- **Path Variable**: `isbn13`

#### 4. 특정 책의 리뷰 목록 조회
```
GET /api/books/{isbn13}/reviews?page={num}&size={num}
```
- **Path Variable**: `isbn13`
- **Query Parameters**: `page`, `size`
- **Response**: `Page<ReviewResponse>` (최신순)

#### 5. 내가 작성한 리뷰 목록 조회
```
GET /api/users/me/reviews?page={num}&size={num}
```
- **Query Parameters**: `page`, `size`
- **Response**: `Page<ReviewResponse>` (책 정보 포함, 최신순)

---

## 추천 도메인 (Recommend Domain)

### SmartSearchController (`/api/search`)

#### 1. AI 기반 스마트 검색
```
POST /api/search/smart
```
- **Request Body**:
  ```json
  {
    "query": "string"
  }
  ```
- **Response**:
  ```json
  {
    "type": "SEARCH|RECOMMEND",
    "matchedCategory": {
      "id": 1,
      "name": "string"
    },
    "books": [],
    "needsCategorySelection": false,
    "categoryOptions": [
      {
        "id": 1,
        "name": "string",
        "similarity": 0.95
      }
    ]
  }
  ```
- **참고**: 의도 분류 + 추천/검색 자동 처리

### CategoryEmbeddingController (Admin: `/api/admin/category-embeddings`)

#### 2. 전체 카테고리 임베딩 생성 (관리자)
```
POST /api/admin/category-embeddings/generate-all
```

#### 3. 특정 카테고리 임베딩 생성 (관리자)
```
POST /api/admin/category-embeddings/{categoryId}
```
- **Path Variable**: `categoryId`

---

## DTO 구조

### Book DTOs

#### BookResponseDto
```kotlin
data class BookResponseDto(
    val isbn13: String,
    val title: String,
    val author: String,
    val cover: String,
    val readingStatus: ReadingStatus?, // NOT_STARTED, READING, COMPLETED, DROPPED
    val startedAt: LocalDateTime?,
    val completedAt: LocalDateTime?
)
```

#### BookDetailDto
```kotlin
data class BookDetailDto(
    val isbn13: String,
    val title: String,
    val author: String,
    val publisher: String,
    val cover: String,
    val description: String,
    val category: CategoryDto?
)
```

#### SimilarBookSearchRequest
```kotlin
data class SimilarBookSearchRequest(
    val query: String,
    val topK: Int = 10
)
```

#### SimilarBookSearchResponse
```kotlin
data class SimilarBookSearchResponse(
    val query: String,
    val books: List<SimilarBookDto>
)

data class SimilarBookDto(
    val isbn13: String,
    val title: String,
    val author: String,
    val similarity: Double
)
```

#### UpdateReadingStatusRequest
```kotlin
data class UpdateReadingStatusRequest(
    val status: ReadingStatus
)

enum class ReadingStatus {
    NOT_STARTED, READING, COMPLETED, DROPPED
}
```

### User DTOs

#### LoginRequest
```kotlin
data class LoginRequest(
    val idToken: String
)
```

#### LoginResponse
```kotlin
data class LoginResponse(
    val jwt: String,
    val email: String,
    val name: String,
    val picture: String
)
```

#### CreateCollectionRequest
```kotlin
data class CreateCollectionRequest(
    val name: String
)
```

#### UpdateCollectionRequest
```kotlin
data class UpdateCollectionRequest(
    val name: String
)
```

#### UserCollectionResponse
```kotlin
data class UserCollectionResponse(
    val id: Long,
    val name: String,
    val bookCount: Int,
    val createdAt: LocalDateTime
)
```

#### CollectionBookResponse
```kotlin
data class CollectionBookResponse(
    val isbn13: String,
    val title: String,
    val author: String,
    val cover: String,
    val addedAt: LocalDateTime
)
```

### Chatbot DTOs

#### ChatbotSearchRequest
```kotlin
data class ChatbotSearchRequest(
    val query: String
)
```

#### ChatbotSearchResponse
```kotlin
data class ChatbotSearchResponse(
    val message: String,
    val books: List<BookDetailDto>
)
```

#### SendMessageRequest
```kotlin
data class SendMessageRequest(
    val message: String
)
```

#### SendMessageResponse
```kotlin
data class SendMessageResponse(
    val sessionId: Long,
    val reply: String,
    val books: List<BookDetailDto>
)
```

#### ChatSessionSummaryDto
```kotlin
data class ChatSessionSummaryDto(
    val id: Long,
    val title: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
```

#### ChatSessionDetailDto
```kotlin
data class ChatSessionDetailDto(
    val id: Long,
    val title: String,
    val messages: List<ChatMessageDto>,
    val createdAt: LocalDateTime
)
```

#### ChatMessageDto
```kotlin
data class ChatMessageDto(
    val role: MessageRole, // USER, ASSISTANT
    val content: String,
    val createdAt: LocalDateTime
)

enum class MessageRole {
    USER, ASSISTANT
}
```

### Review DTOs

#### ReviewRequest
```kotlin
data class ReviewRequest(
    val content: String
)
```

#### ReviewResponse
```kotlin
data class ReviewResponse(
    val id: Long,
    val userName: String,
    val userPicture: String,
    val content: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    // 내 리뷰 목록 조회 시 포함
    val isbn13: String?,
    val bookTitle: String?,
    val bookCover: String?
)
```

### Recommend DTOs

#### SmartSearchRequest
```kotlin
data class SmartSearchRequest(
    val query: String
)
```

#### SmartSearchResponse
```kotlin
data class SmartSearchResponse(
    val type: SearchType, // SEARCH, RECOMMEND
    val matchedCategory: CategoryDto?,
    val books: List<BookDetailDto>,
    val needsCategorySelection: Boolean,
    val categoryOptions: List<CategoryOptionDto>
)

enum class SearchType {
    SEARCH, RECOMMEND
}
```

#### CategoryDto
```kotlin
data class CategoryDto(
    val id: Long,
    val name: String
)
```

#### CategoryOptionDto
```kotlin
data class CategoryOptionDto(
    val id: Long,
    val name: String,
    val similarity: Double
)
```

---

## 주요 엔티티

### Book Entity
```kotlin
@Entity
class Book(
    @Id val isbn13: String,
    val title: String,
    val author: String,
    val publisher: String,
    val cover: String,
    val description: String,
    @ManyToOne val category: Category?,
    @OneToMany val userSavedBooks: List<UserSavedBook>
)
```

### User Entity
```kotlin
@Entity
class User(
    @Id @GeneratedValue val id: Long,
    @Column(unique = true) val email: String,
    val name: String,
    val picture: String,
    val provider: String, // google, kakao, naver
    val providerId: String,
    val role: UserRole, // USER, ADMIN
    @OneToMany val preferences: List<UserPreference>,
    @OneToMany val searchLogs: List<SearchLog>,
    @OneToMany val savedBooks: List<UserSavedBook>
)

enum class UserRole {
    USER, ADMIN
}
```

### UserSavedBook Entity
```kotlin
@Entity
class UserSavedBook(
    @Id @GeneratedValue val id: Long,
    @ManyToOne val user: User,
    @ManyToOne val book: Book,
    val readingStatus: ReadingStatus,
    val startedAt: LocalDateTime?,
    val completedAt: LocalDateTime?,
    val savedAt: LocalDateTime
)
```

---

## 인증 방식

모든 API는 JWT 기반 인증을 사용합니다.

### 헤더 설정
```
Authorization: Bearer {JWT_TOKEN}
```

### 토큰 갱신
- 현재 구현된 명시적인 토큰 갱신 API는 없음
- 토큰 만료 시 재로그인 필요

---

## 외부 API 통합

### Aladin API
- 도서 상세 정보 조회
- 베스트셀러 목록 조회

### Gemini API
- AI 기반 의도 분류
- 자연어 처리
- 스마트 검색 쿼리 분석

---

## 특징적인 기능

### 1. RAG 기반 검색
- 도서 임베딩을 활용한 의미 기반 유사 도서 검색
- 벡터 유사도 계산으로 정확한 추천

### 2. 적응형 추천
- 사용자 선호도 자동 학습
- 카테고리 기반 개인화 추천

### 3. 대화형 챗봇
- 세션 기반 대화 관리
- 컨텍스트 유지
- 책 검색/추천 통합

### 4. 컬렉션 관리
- 사용자별 책 컬렉션 생성
- 컬렉션별 책 관리
- 독서 목록 구성

### 5. 독서 상태 추적
- NOT_STARTED: 읽지 않음
- READING: 읽는 중
- COMPLETED: 완독
- DROPPED: 중단

### 6. 리뷰 시스템
- 내 서재 책에 대한 리뷰 작성
- 리뷰 수정/삭제
- 책별 리뷰 목록 조회

---

## 페이징 응답 구조

Spring Data의 `Page` 객체를 사용합니다.

```kotlin
data class Page<T>(
    val content: List<T>,
    val pageable: Pageable,
    val totalPages: Int,
    val totalElements: Long,
    val last: Boolean,
    val first: Boolean,
    val size: Int,
    val number: Int,
    val numberOfElements: Int,
    val empty: Boolean
)
```

---

## 에러 응답 구조

```kotlin
data class ErrorResponse(
    val timestamp: LocalDateTime,
    val status: Int,
    val error: String,
    val message: String,
    val path: String
)
```

---

## 개발 우선순위 추천

### Phase 1: 기본 기능 (MVP)
1. 인증 (OAuth 로그인)
2. 홈 화면 (베스트셀러)
3. 도서 상세
4. 내 서재 (저장/삭제/상태 변경)

### Phase 2: 핵심 기능
5. 스마트 검색
6. 유사 도서 검색
7. 리뷰 작성/조회

### Phase 3: 고급 기능
8. 컬렉션 관리
9. 챗봇 대화
10. 사용자 프로필

---

## Base URL

개발 환경에 따라 설정:
- 로컬: `http://localhost:8080`
- 개발 서버: `https://dev-api.readpick.com`
- 프로덕션: `https://api.readpick.com`

---

**문서 버전**: 1.0
**최종 업데이트**: 2025-10-30
**작성자**: Claude Code
