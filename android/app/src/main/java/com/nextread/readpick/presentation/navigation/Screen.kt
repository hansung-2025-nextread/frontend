package com.nextread.readpick.presentation.navigation

/**
 * 앱 내 모든 화면의 Route를 정의하는 sealed class
 *
 * 각 화면은 고유한 route 문자열을 가지며, Navigation Compose에서 사용됩니다.
 * 새로운 화면을 추가할 때는 이 클래스에 새로운 object를 추가하세요.
 */
sealed class Screen(val route: String) {
    /**
     * 로그인 화면
     * - Google OAuth 인증
     * - 로그인 성공 시 온보딩 상태 확인 후 Onboarding 또는 Home으로 이동
     */
    data object Login : Screen("login")

    /**
     * 온보딩 화면
     * - 신규 사용자의 관심 카테고리 선택
     * - 8개 카테고리 중 선택 또는 건너뛰기 가능
     * - 완료 후 Home으로 이동
     */
    data object Onboarding : Screen("onboarding")

    /**
     * 홈 화면
     * - 베스트셀러 목록
     * - 맞춤 추천 도서
     */
    data object Home : Screen("home")

    // TODO: 팀원들이 추가할 화면들
    // data object BookDetail : Screen("book/{isbn13}") - 팀원1
    // data object Search : Screen("search") - 팀원1
    data object Chatbot : Screen("chatbot")
    // data object Review : Screen("review") - 팀원2
    // data object MyPage : Screen("mypage") - 팀원3
    // data object Collection : Screen("collection") - 팀원3
}
