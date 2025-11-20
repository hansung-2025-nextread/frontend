package com.nextread.readpick.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nextread.readpick.presentation.auth.login.LoginScreen
import com.nextread.readpick.presentation.onboarding.OnboardingScreen
import com.nextread.readpick.presentation.chatbot.ChatBotScreen

// --------------------------------------------------------
// 🚨 1. 우리가 만든 실제 HomeScreen을 import 합니다.
// --------------------------------------------------------
import com.nextread.readpick.presentation.home.HomeScreen

/**
 * ReadPick 앱의 전체 Navigation Graph
 *
 * @param navController 화면 전환을 관리하는 NavController
 * @param startDestination 앱 시작 시 표시할 화면
 */
@Composable
fun ReadPickNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Chatbot.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 로그인 화면
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { needsOnboarding ->
                    if (needsOnboarding) {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        // 온보딩 화면
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onOnboardingComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onMenuClick = { /* TODO: 네비게이션 드로어 열기 */ },

                // (이하 버튼들은 Screen.kt에 경로 추가 후 연결 필요)
                onSearchClick = {
                    // TODO: (팀원1) Screen.Search 추가 후 연결
                },
                onChatbotClick = {
                    navController.navigate(Screen.Chatbot.route)
                },
                onMyLibraryClick = {
                    // TODO: (팀원3) Screen.Collection 추가 후 연결
                },
                onMyPageClick = {
                    // TODO: (팀원3) Screen.MyPage 추가 후 연결
                },
                onBookClick = { isbn13 ->
                    // HomeScreen에서 전달받은 isbn13을 사용
                    navController.navigate(Screen.BookDetail.createRoute(isbn13))
                }
            )
        }

        // TODO: 팀원들이 아래에 각자 화면 추가
        // 예시:
        // composable(Screen.BookDetail.route) { BookDetailScreen(...) }
        // composable(Screen.Search.route) { SearchScreen(...) }

        composable(Screen.Chatbot.route) { ChatBotScreen() }
    }
}
