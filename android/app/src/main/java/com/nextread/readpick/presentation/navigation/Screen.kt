package com.nextread.readpick.presentation.navigation

/**
 * 앱 내 모든 화면의 Route를 정의하는 sealed class
 *
 * 각 화면은 고유한 route 문자열을 가지며, Navigation Compose에서 사용됩니다.
 * 새로운 화면을 추가할 때는 이 클래스에 새로운 object를 추가하세요.
 */
sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")

    // --------------------------------------------------------
    // 🚨 [수정] 팀원1 담당 화면 주석 해제
    // --------------------------------------------------------
    /**
     * 도서 상세 화면
     * @param isbn13 책 고유 ID
     */
    data object BookDetail : Screen("book/{isbn13}") {
        fun createRoute(isbn13: String) = "book/$isbn13"
    }

    /**
     * 검색 화면
     */
    data object Search : Screen("search")

    // TODO: 팀원들이 추가할 화면들
    data object Chatbot : Screen("chatbot")
    data object Review : Screen("review")
    data object MyPage : Screen("mypage")

    data object MyLibrary : Screen("mylibrary")

    /**
     * 관리자 대시보드 화면
     * ADMIN 권한을 가진 사용자만 접근 가능
     */
    data object AdminDashboard : Screen("admin/dashboard")
}