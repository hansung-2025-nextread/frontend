# ReadPick 안드로이드 팀 역할 분담

> 4명 팀 효율적 협업 가이드

## 👥 팀 구성

```
팀 구성: 4명
개발 기간: 2-3주
아키텍처: Clean Architecture (MVVM + Compose)
```

---

## 📋 역할 분담 요약

| 담당자 | 역할 | 화면 수 | 난이도 | 예상 시간 |
|--------|------|---------|--------|-----------|
| **팀장** | 공통 인프라 + 인증 | 2개 | ⭐⭐⭐⭐⭐ | 30시간 |
| **팀원 1** | 도서 관련 | 3개 | ⭐⭐⭐⭐ | 25시간 |
| **팀원 2** | 챗봇 + 리뷰 | 2개 | ⭐⭐⭐ | 20시간 |
| **팀원 3** | 마이페이지 + 컬렉션 | 2개 | ⭐⭐⭐ | 20시간 |

**총 작업량**: ~100시간 → 4명 병렬 작업 시 **2-3주 완성 가능**

---

## 👨‍💼 팀장 - 공통 인프라 + 인증

### 🎯 핵심 역할
**모든 팀원이 사용할 기반을 먼저 구축** (가장 중요!)

### 📦 담당 작업

#### 1. 공통 인프라 구축 (Priority: 최우선)
```
✅ Retrofit 설정
✅ OkHttp + Interceptor (JWT 자동 추가)
✅ Hilt DI 모듈
✅ DataStore (토큰 관리)
✅ 공통 DTO 베이스 클래스
✅ 에러 처리 공통 로직
✅ 공통 UI 컴포넌트
```

#### 2. 인증 기능
```
- 스플래시 화면
- 로그인 화면 (OAuth: Google, Kakao, Naver)
- 자동 로그인 처리
```

#### 3. 네비게이션 설정
```
- NavGraph 설정
- Route 정의
- DeepLink 설정
```

#### 4. 프로젝트 관리
```
- Git 브랜치 전략 수립
- PR 리뷰
- 빌드 이슈 해결
- 공통 코드 관리
```

### 📁 작업 파일 구조
```
di/
  ├── NetworkModule.kt          # Retrofit, OkHttp 설정
  ├── DataStoreModule.kt        # DataStore 설정
  └── RepositoryModule.kt       # Repository 바인딩

data/local/
  └── TokenManager.kt           # JWT 토큰 저장/조회

data/remote/interceptor/
  ├── AuthInterceptor.kt        # JWT 자동 추가
  └── LoggingInterceptor.kt     # 네트워크 로깅

data/model/common/
  ├── ApiResponse.kt            # 공통 API 응답
  └── ErrorResponse.kt          # 에러 응답

data/model/auth/
  ├── LoginRequest.kt
  └── LoginResponse.kt

data/remote/api/
  └── AuthApi.kt                # 인증 API

data/repository/
  └── AuthRepositoryImpl.kt     # 인증 Repository

presentation/auth/login/
  ├── LoginScreen.kt
  ├── LoginViewModel.kt
  └── LoginUiState.kt

presentation/common/
  ├── component/
  │   ├── BookCard.kt           # 공통 책 카드
  │   ├── LoadingIndicator.kt   # 로딩 표시
  │   ├── ErrorMessage.kt       # 에러 메시지
  │   ├── SearchBar.kt          # 검색창
  │   └── EmptyState.kt         # 빈 상태
  └── navigation/
      ├── NavGraph.kt           # 네비게이션 설정
      └── Screen.kt             # Route 정의
```

### 📝 API 연동
```kotlin
POST /auth/{provider}           # OAuth 로그인
```

### ⏰ 작업 일정
- **Day 1-2**: 공통 인프라 구축 (최우선!)
- **Day 3-4**: 공통 UI 컴포넌트
- **Day 5-6**: 로그인 화면 구현
- **Day 7**: 네비게이션 통합 및 테스트

---

## 👨‍💻 팀원 1 - 도서 관련

### 🎯 핵심 역할
**앱의 메인 기능 (홈, 검색, 도서 상세)**

### 📦 담당 작업

#### 1. 홈 화면
```
- 베스트셀러 목록 표시
- 카테고리별 책 목록
- 새로고침 기능
```

#### 2. 검색 화면
```
- AI 스마트 검색
- 검색 결과 표시
- 검색 기록 관리
```

#### 3. 도서 상세 화면
```
- 책 상세 정보 표시
- 내 서재 저장/삭제
- 독서 상태 변경
- 리뷰 목록 연동 (팀원 2와 협업)
```

### 📁 작업 파일 구조
```
data/model/book/
  ├── BookResponseDto.kt
  ├── BookDetailDto.kt
  └── UpdateReadingStatusRequest.kt

data/model/recommend/
  ├── SmartSearchRequest.kt
  └── SmartSearchResponse.kt

data/remote/api/
  ├── BookApi.kt
  └── SearchApi.kt

data/repository/
  ├── BookRepositoryImpl.kt
  └── SearchRepositoryImpl.kt

domain/repository/
  ├── BookRepository.kt
  └── SearchRepository.kt

presentation/home/
  ├── HomeScreen.kt
  ├── HomeViewModel.kt
  └── HomeUiState.kt

presentation/search/
  ├── SearchScreen.kt
  ├── SearchViewModel.kt
  └── SearchUiState.kt

presentation/book/
  ├── detail/
  │   ├── BookDetailScreen.kt
  │   ├── BookDetailViewModel.kt
  │   └── BookDetailUiState.kt
  └── list/
      ├── BookListScreen.kt
      └── BookListViewModel.kt
```

### 📝 API 연동
```kotlin
GET  /api/books/bestsellers                  # 베스트셀러 조회
GET  /api/books/{isbn13}                     # 도서 상세 조회
POST /api/books/{isbn13}                     # 내 서재에 저장
DELETE /api/books/{isbn13}                   # 내 서재에서 삭제
PUT  /api/books/saved/{isbn13}/status        # 독서 상태 변경
POST /api/search/smart                       # 스마트 검색
```

### ⏰ 작업 일정
- **Day 1-2**: DTO 및 API 인터페이스 작성
- **Day 3-4**: 홈 화면 구현
- **Day 5-6**: 도서 상세 화면 구현
- **Day 7**: 검색 화면 구현

### 🤝 협업 포인트
- 팀원 2와 리뷰 목록 연동
- 팀장의 공통 `BookCard` 컴포넌트 사용

---

## 👨‍💻 팀원 2 - 챗봇 + 리뷰

### 🎯 핵심 역할
**대화형 기능 (챗봇, 리뷰)**

### 📦 담당 작업

#### 1. 챗봇 화면
```
- 대화 세션 관리
- 메시지 전송/수신
- 책 추천 결과 표시
- 대화 기록 조회
```

#### 2. 리뷰 기능
```
- 리뷰 작성/수정/삭제
- 책별 리뷰 목록 조회
- 내가 쓴 리뷰 목록
```

### 📁 작업 파일 구조
```
data/model/chatbot/
  ├── ChatSessionDto.kt
  ├── ChatMessageDto.kt
  ├── SendMessageRequest.kt
  └── SendMessageResponse.kt

data/model/review/
  ├── ReviewRequest.kt
  └── ReviewResponse.kt

data/remote/api/
  ├── ChatbotApi.kt
  └── ReviewApi.kt

data/repository/
  ├── ChatbotRepositoryImpl.kt
  └── ReviewRepositoryImpl.kt

domain/repository/
  ├── ChatbotRepository.kt
  └── ReviewRepository.kt

presentation/chatbot/
  ├── ChatbotScreen.kt
  ├── ChatbotViewModel.kt
  ├── ChatbotUiState.kt
  └── components/
      ├── MessageBubble.kt
      └── ChatInput.kt

presentation/review/
  ├── ReviewScreen.kt
  ├── ReviewViewModel.kt
  ├── ReviewUiState.kt
  └── components/
      ├── ReviewCard.kt
      └── ReviewForm.kt
```

### 📝 API 연동
```kotlin
# 챗봇
POST   /api/chatbot/conversations/sessions                     # 새 대화 생성
GET    /api/chatbot/conversations/sessions                     # 대화 목록
GET    /api/chatbot/conversations/sessions/{sessionId}         # 대화 상세
POST   /api/chatbot/conversations/sessions/{sessionId}/messages # 메시지 전송
DELETE /api/chatbot/conversations/sessions/{sessionId}         # 대화 삭제

# 리뷰
POST   /api/books/{isbn13}/reviews        # 리뷰 작성
PUT    /api/books/{isbn13}/reviews        # 리뷰 수정
DELETE /api/books/{isbn13}/reviews        # 리뷰 삭제
GET    /api/books/{isbn13}/reviews        # 책의 리뷰 목록
GET    /api/users/me/reviews              # 내가 쓴 리뷰
```

### ⏰ 작업 일정
- **Day 1-2**: DTO 및 API 인터페이스 작성
- **Day 3-4**: 챗봇 화면 구현
- **Day 5-6**: 리뷰 기능 구현
- **Day 7**: 통합 테스트

### 🤝 협업 포인트
- 팀원 1의 도서 상세 화면과 리뷰 목록 연동
- 팀원 3의 마이페이지와 "내가 쓴 리뷰" 연동

---

## 👨‍💻 팀원 3 - 마이페이지 + 컬렉션

### 🎯 핵심 역할
**사용자 관리 (마이페이지, 컬렉션)**

### 📦 담당 작업

#### 1. 마이페이지
```
- 내 서재 (저장된 책 목록)
- 내가 쓴 리뷰 목록
- 프로필 정보
- 독서 통계
```

#### 2. 컬렉션 관리
```
- 컬렉션 생성/수정/삭제
- 컬렉션 목록 조회
- 컬렉션에 책 추가/제거
- 컬렉션 내 책 목록
```

### 📁 작업 파일 구조
```
data/model/user/
  ├── UserDto.kt
  ├── SavedBookDto.kt
  ├── CreateCollectionRequest.kt
  ├── UpdateCollectionRequest.kt
  └── CollectionDto.kt

data/remote/api/
  ├── UserApi.kt
  └── CollectionApi.kt

data/repository/
  ├── UserRepositoryImpl.kt
  └── CollectionRepositoryImpl.kt

domain/repository/
  ├── UserRepository.kt
  └── CollectionRepository.kt

presentation/mypage/
  ├── MyPageScreen.kt
  ├── MyPageViewModel.kt
  ├── MyPageUiState.kt
  └── components/
      ├── SavedBookList.kt
      ├── MyReviewList.kt
      └── ProfileSection.kt

presentation/collection/
  ├── CollectionListScreen.kt
  ├── CollectionDetailScreen.kt
  ├── CollectionViewModel.kt
  ├── CollectionUiState.kt
  └── components/
      ├── CollectionCard.kt
      └── CollectionDialog.kt
```

### 📝 API 연동
```kotlin
# 마이페이지
GET /api/users/me/saved-books          # 내 서재
GET /api/users/me/reviews              # 내가 쓴 리뷰

# 컬렉션
POST   /api/users/me/collections                              # 컬렉션 생성
GET    /api/users/me/collections                              # 컬렉션 목록
PUT    /api/users/me/collections/{collectionId}               # 컬렉션 수정
DELETE /api/users/me/collections/{collectionId}               # 컬렉션 삭제
POST   /api/users/me/collections/{collectionId}/books/{isbn13} # 책 추가
DELETE /api/users/me/collections/{collectionId}/books/{isbn13} # 책 제거
GET    /api/users/me/collections/{collectionId}/books          # 컬렉션 책 목록
```

### ⏰ 작업 일정
- **Day 1-2**: DTO 및 API 인터페이스 작성
- **Day 3-4**: 마이페이지 구현
- **Day 5-6**: 컬렉션 관리 구현
- **Day 7**: 통합 테스트

### 🤝 협업 포인트
- 팀원 1의 도서 카드 컴포넌트 재사용
- 팀원 2의 리뷰 목록 연동

---

## 📅 전체 개발 일정

### Week 1: 인프라 + 기본 화면

| Day | 팀장 | 팀원 1 | 팀원 2 | 팀원 3 |
|-----|------|--------|--------|--------|
| **1** | Retrofit + Hilt 설정 | 대기 (인프라 완성 후) | 대기 | 대기 |
| **2** | OkHttp + DataStore | DTO 작성 시작 | DTO 작성 시작 | DTO 작성 시작 |
| **3** | 공통 컴포넌트 | 홈 화면 UI | 챗봇 화면 UI | 마이페이지 UI |
| **4** | 로그인 화면 | 홈 화면 로직 | 챗봇 로직 | 마이페이지 로직 |
| **5** | 네비게이션 설정 | 도서 상세 UI | 리뷰 UI | 컬렉션 UI |
| **6** | 로그인 통합 | 도서 상세 로직 | 리뷰 로직 | 컬렉션 로직 |
| **7** | 전체 통합 테스트 | 검색 화면 | 통합 테스트 | 통합 테스트 |

### Week 2: 완성 + 테스트

| Day | 팀장 | 팀원 1 | 팀원 2 | 팀원 3 |
|-----|------|--------|--------|--------|
| **1** | 버그 수정 | 검색 로직 | 챗봇 개선 | 컬렉션 상세 |
| **2** | 전체 리뷰 | 상태 관리 개선 | 리뷰 목록 | 내 서재 필터 |
| **3** | 성능 최적화 | UI 개선 | UI 개선 | UI 개선 |
| **4** | 최종 통합 | 최종 테스트 | 최종 테스트 | 최종 테스트 |
| **5** | 배포 준비 | 문서 작성 | 문서 작성 | 문서 작성 |

---

## 🔄 Git 브랜치 전략

### 브랜치 구조
```
main (프로덕션 - 직접 푸시 금지)
  ↓
develop (개발 통합 - 팀장 관리)
  ↓
├── feature/infrastructure    (팀장 - Day 1-2)
├── feature/auth-login        (팀장 - Day 3-6)
├── feature/common-components (팀장 - Day 3-4)
├── feature/navigation        (팀장 - Day 5)
├── feature/home-screen       (팀원1 - Day 3-4)
├── feature/book-detail       (팀원1 - Day 5-6)
├── feature/search            (팀원1 - Day 7)
├── feature/chatbot           (팀원2 - Day 3-4)
├── feature/review            (팀원2 - Day 5-6)
├── feature/mypage            (팀원3 - Day 3-4)
└── feature/collection        (팀원3 - Day 5-6)
```

### 작업 순서

#### 🎯 Day 1-2: 팀장만 작업
```bash
# 1. 팀장: 인프라 브랜치 생성
git checkout -b feature/infrastructure

# 2. 인프라 구축
- NetworkModule.kt
- DataStoreModule.kt
- AuthInterceptor.kt
- 공통 DTO

# 3. develop에 머지
git checkout develop
git merge feature/infrastructure
git push origin develop
```

#### 🎯 Day 3~: 팀원들 동시 작업
```bash
# 각 팀원: develop에서 브랜치 생성
git checkout develop
git pull origin develop
git checkout -b feature/home-screen  # 각자 브랜치명

# 작업 완료 후 PR 생성
git push origin feature/home-screen
# GitHub에서 PR 생성: feature/home-screen → develop
```

### PR 규칙
```markdown
## PR 제목 형식
[화면명] 간단한 설명

예시:
- [홈] 베스트셀러 목록 구현
- [챗봇] 메시지 전송 기능 추가
- [인프라] Retrofit 설정 완료

## PR 설명 템플릿
### 작업 내용
- 구현한 기능 1
- 구현한 기능 2

### 스크린샷 (있으면)
![screenshot](image.png)

### 체크리스트
- [ ] 빌드 성공
- [ ] 코드 포맷팅 완료
- [ ] 주석 작성 완료
- [ ] 테스트 완료
```

---

## 🤝 협업 규칙

### 1. Daily Standup (매일 10분)
```
시간: 매일 오전 10시
방식: Slack/Discord 텍스트

포맷:
[이름]
어제: 뭘 했나?
오늘: 뭘 할 건가?
블로커: 막힌 거 있나?

예시:
[팀장]
어제: Retrofit 설정 완료
오늘: DataStore 구현
블로커: 없음

[팀원1]
어제: DTO 작성 완료
오늘: 홈 화면 UI 작업
블로커: 공통 BookCard 컴포넌트 필요
```

### 2. 코드 리뷰 규칙
```
- PR 생성 후 24시간 내 리뷰
- 2명 이상 Approve 필요 (팀장 + 1명)
- 블로킹 이슈는 바로 Slack 멘션
```

### 3. 커밋 메시지 규칙
```
[타입] 간단한 설명

타입:
- feat: 새 기능
- fix: 버그 수정
- refactor: 리팩토링
- style: 코드 포맷팅
- docs: 문서 수정

예시:
feat: 홈 화면 베스트셀러 목록 구현
fix: 로그인 시 토큰 저장 안 되는 버그 수정
refactor: BookCard 컴포넌트 재사용 가능하게 변경
```

### 4. 충돌 방지 규칙
```
❌ 금지 사항:
1. 같은 파일 동시 수정
2. develop 브랜치에 직접 푸시
3. 리뷰 없이 머지
4. 공통 코드 각자 작성

✅ 권장 사항:
1. 작업 전 develop pull
2. 자주 커밋 (1일 1커밋 이상)
3. 막히면 바로 물어보기
4. 공통 컴포넌트는 팀장과 상의
```

---

## 🛠️ 독립 작업 방법 (인프라 완성 전)

### Mock Repository 사용

#### 예시 1: 팀원 1 - 홈 화면
```kotlin
// 1. Mock Repository 생성
class MockBookRepository : BookRepository {
    override suspend fun getBestsellers(): Result<List<Book>> {
        delay(500) // 네트워크 시뮬레이션
        return Result.success(
            listOf(
                Book("123", "1984", "조지 오웰", "https://..."),
                Book("456", "동물농장", "조지 오웰", "https://...")
            )
        )
    }
}

// 2. ViewModel에서 사용
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bookRepository: BookRepository  // Mock으로 교체 가능
) : ViewModel() {
    // UI 로직 구현
}

// 3. 인프라 완성 후 DI로 자동 교체
// MockBookRepository → BookRepositoryImpl
```

#### 예시 2: 팀원 2 - 챗봇
```kotlin
// Mock 데이터로 UI 먼저 완성
class MockChatbotRepository : ChatbotRepository {
    override suspend fun sendMessage(message: String): Result<ChatMessage> {
        delay(300)
        return Result.success(
            ChatMessage(
                role = MessageRole.ASSISTANT,
                content = "Mock 응답: $message"
            )
        )
    }
}
```

---

## 📞 커뮤니케이션 채널

### Slack/Discord 채널 구성
```
#general          - 일반 대화
#dev-android      - 개발 관련
#standup          - 데일리 스탠드업
#pr-review        - PR 알림
#build-status     - 빌드 실패 알림
```

### 긴급 연락 순서
```
1. Slack 멘션 (@팀장)
2. 전화 (급한 경우)
3. 회의 소집 (블로킹 이슈)
```

---

## 🎯 성공 체크리스트

### Week 1 종료 시점
- [ ] 팀장: 공통 인프라 완성
- [ ] 팀장: 로그인 화면 완성
- [ ] 팀원 1: 홈 화면 + 도서 상세 완성
- [ ] 팀원 2: 챗봇 화면 완성
- [ ] 팀원 3: 마이페이지 완성
- [ ] 전원: 각자 화면에서 Mock 데이터로 테스트 가능

### Week 2 종료 시점
- [ ] 전체 네비게이션 연결
- [ ] 실제 API 연동 완료
- [ ] 로그인부터 주요 기능까지 E2E 테스트 성공
- [ ] 버그 수정 완료
- [ ] 문서 작성 완료

---

## 🚨 주의사항

### 실패 요인
1. **팀장이 인프라를 늦게 완성** → 전체 지연
2. **커뮤니케이션 부족** → 중복 작업, 충돌
3. **공통 코드 각자 작성** → 통합 시 문제
4. **리뷰 없이 머지** → 버그 증가

### 성공 요인
1. **팀장이 Day 1-2에 인프라 완성** ⭐
2. **매일 짧은 스탠드업** ⭐
3. **공통 컴포넌트 미리 제공** ⭐
4. **자주 커밋, 자주 머지** ⭐
5. **막히면 바로 물어보기** ⭐

---

## 📚 참고 문서

- [BACKEND_API_REFERENCE.md](./BACKEND_API_REFERENCE.md) - 백엔드 API 문서
- [PROJECT_STRUCTURE.md](./PROJECT_STRUCTURE.md) - 프로젝트 구조
- [INFRASTRUCTURE_GUIDE.md](./INFRASTRUCTURE_GUIDE.md) - 공통 인프라 가이드 (다음 문서)

---

**문서 버전**: 1.0
**최종 업데이트**: 2025-10-30
**작성자**: Claude Code
