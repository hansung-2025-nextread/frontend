## 1. 역할 분담
- **팀장 (백엔드 담당, Spring Boot)**
    - API 개발 및 관리 (도서 검색, 기록, 상세보기, 추천)
    - 데이터베이스 설계 및 크롤링/추천 알고리즘 연동
    - Swagger로 API 문서화 및 목업 API 제공

- **팀원들 (프론트, Android Studio)**
    - 앱 UI/UX 구현 (Kotlin/Java)
    - Retrofit을 활용해 백엔드 API 호출 및 데이터 화면 표시
    - 독서 기록, 검색, 추천 UI 제작

---

## 2. 프로젝트 단계별 진행 계획

### 📌 1단계 (2~3주)
- 백엔드: 목업 API 제공, Swagger 문서 완료
- 프론트: 안드로이드 환경 세팅, 기본 화면(온보딩, 홈) 구성

### 📌 2단계 (4~6주)
- 백엔드: 도서 API/크롤링 연동, DB 저장
- 프론트: 검색 API 연동 → 책 리스트 출력

### 📌 3단계 (7~10주)
- 백엔드: 기록 API, 상세보기 API 구축
- 프론트: 독서 기록 화면, 상세보기 화면 제작

### 📌 4단계 (마무리)
- 백엔드: 추천 시스템(FastAPI + GPT) 연동
- 프론트: 홈 화면에 맞춤형 도서 추천 표시

---

## 3. 프론트 학습 로드맵 (필수 기술)
- **UI**: RecyclerView, ConstraintLayout, JetPackCompose ... 어떤 기술인지 찾아봐야함.
- **네트워크**: Retrofit2 (API 통신) -> 앱과 서버의 통신 쉽게 해주는 라이브러리
- **로컬 데이터**: Room (최근 검색어 저장)
- **상태 관리**: ViewModel + LiveData
- **Android Studio + Java or Kotlin**

👉 첫 번째 목표: Retrofit으로 목업 API 호출 → 책 리스트 화면에 띄우기<br>

-> 어떻게 프론트에서 서버에 있는 목업 API 호출하나? -> ngork로 외부에서 접속 가능한 URL 드리면 base url 설정 후 호출해보기  

---

## 4. 협업 규칙
- Git 브랜치 전략
    - `main`: 안정적인 버전
    - `backend/*`, `frontend/*`: 작업용 브랜치
- Pull Request(PR) 규칙
    - 각자 브랜치에서 작업 후 PR 생성 → 코드 리뷰 → main에 머지
- 매주 회의: API 업데이트 & 프론트 개발 상황 공유

---

## 5. 팀장 메시지 
1주차 
> 저는 백엔드(Spring Boot) API를 맡고, 여러분들은 안드로이드 앱에서 UI/UX와 API 호출을 맡으면 됩니다.  
> 우선 목업 API에서 Retrofit으로 책 리스트를 불러와서 화면에 띄우는 것부터 시작해봅시다.  
> 제가 API 명세는 Swagger로 공유하겠습니다."
---

📡 목업 API 외부 접속 주소 (임시)
Base URL: https://<임의>.ngrok-free.app/v1/

엔드포인트:
- GET /books/bestsellers?size=10
- GET /books/{id}

Swagger:
- https://<임의>.ngrok-free.app/v1/swagger-ui/index.html

주의:
- 주소는 ngrok 재시작 시 바뀔 수 있음. 바뀌면 여기 공지할게요.

