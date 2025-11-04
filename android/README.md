# ReadPick Android 📚

AI 기반 도서 추천 서비스 Android 앱

## 🚀 시작하기

### 0. 사전 요구사항

**필수 소프트웨어:**
- ✅ **Java 17 이상** (JDK 17+)
- ✅ **Android Studio** (최신 버전 권장)

**Java 17 설치 확인:**
```bash
java -version
# 출력: java version "17.0.x" 이상이어야 함
```

**Java 17 설치 방법:**
- **macOS**: `brew install openjdk@17`
- **Windows/Linux**: [Amazon Corretto 17](https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html) 다운로드

---

### 1. 프로젝트 Clone

```bash
git clone https://github.com/hansung-2025-nextread/frontend.git
cd frontend/android
```

### 2. Firebase 설정

#### 2-1. Firebase Console에서 google-services.json 다운로드

1. [Firebase Console](https://console.firebase.google.com/) 접속
2. 프로젝트 선택: `nextread-472512`
3. 프로젝트 설정 → 일반 탭
4. 내 앱 → Android 앱 추가 (또는 기존 앱 선택)
   - 패키지 이름: `com.nextread.readpick`
5. `google-services.json` 다운로드
6. `/android/app/` 경로에 복사

#### 2-2. SHA-1 인증서 등록

**자신의 디버그 키스토어 SHA-1 확인:**

```bash
cd android
./gradlew signingReport
```

출력에서 `Variant: debug`의 `SHA1` 값을 복사

**Firebase Console에 등록:**

1. Firebase Console → 프로젝트 설정 → 일반
2. Android 앱 선택
3. "SHA 인증서 지문" 섹션에서 "지문 추가"
4. 복사한 SHA-1 입력 후 저장

### 3. 백엔드 URL 설정

#### local.properties 파일 생성

`android/local.properties` 파일에 다음 추가:

```properties
# 백엔드 API URL (ngrok 또는 실제 서버 URL)
BASE_URL=https://YOUR_NGROK_URL.ngrok-free.app/
```

**예시:**
```properties
BASE_URL=https://abc123.ngrok-free.app/
```

**참고:**
- ngrok 재시작 시 이 값만 변경하면 됨
- 실제 서버 배포 시: `https://api.readpick.com/`

### 4. 빌드 및 실행

```bash
./gradlew assembleDebug
# 또는 Android Studio에서 Run 버튼 클릭
```

---

## 📁 프로젝트 구조

```
app/
├── src/main/java/com/nextread/readpick/
│   ├── presentation/          # UI Layer (Compose)
│   │   ├── auth/login/       # 로그인 화면
│   │   └── common/           # 공통 UI 컴포넌트
│   ├── domain/               # Domain Layer (비즈니스 로직)
│   │   └── repository/       # Repository 인터페이스
│   ├── data/                 # Data Layer
│   │   ├── repository/       # Repository 구현체
│   │   ├── remote/           # 네트워크 (Retrofit)
│   │   │   ├── api/         # API 인터페이스
│   │   │   └── interceptor/ # Interceptor (JWT, Logging)
│   │   ├── local/           # 로컬 저장소 (DataStore)
│   │   └── model/           # DTOs
│   ├── di/                   # Dependency Injection (Hilt)
│   └── ReadPickApplication   # Application 클래스
└── google-services.json       # Firebase 설정 (각자 다운로드)
```

---

## 🔧 기술 스택

- **UI**: Jetpack Compose
- **아키텍처**: Clean Architecture + MVVM
- **DI**: Hilt
- **네트워크**: Retrofit + OkHttp
- **비동기**: Coroutines + Flow
- **인증**: Firebase Auth (Google OAuth)
- **로컬 저장소**: DataStore
- **이미지 로딩**: Coil

---

## 👥 팀원 설정 체크리스트

각 팀원이 다음을 완료해야 합니다:

- [ ] `google-services.json` 다운로드 및 배치
- [ ] 자신의 SHA-1 인증서를 Firebase에 등록
- [ ] `local.properties`에 `BASE_URL` 추가
- [ ] 빌드 성공 확인
- [ ] Google 로그인 테스트

---

## 🐛 문제 해결

### 1. "google-services.json not found" 에러

**원인:** Firebase 설정 파일이 없음

**해결:**
```bash
# google-services.json.example을 참고하여 실제 파일 생성
cp app/google-services.json.example app/google-services.json
# Firebase Console에서 실제 파일 다운로드 후 교체
```

### 2. "No credentials available" (Google 로그인 실패)

**원인:** SHA-1 인증서가 Firebase에 등록되지 않음

**해결:**
```bash
./gradlew signingReport
# SHA1 값을 Firebase Console에 등록
```

### 3. "HTTP 404" 또는 "UnknownHostException"

**원인:** `BASE_URL`이 잘못 설정됨

**해결:**
```properties
# local.properties 확인
BASE_URL=https://올바른_ngrok_URL.ngrok-free.app/
```

---

## 📝 참고 문서

- [Firebase Android 설정](https://firebase.google.com/docs/android/setup)
- [Google Sign-In](https://developers.google.com/identity/sign-in/android/start)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)

---

## 📞 문의

문제가 발생하면 팀 채널에 문의하세요!
