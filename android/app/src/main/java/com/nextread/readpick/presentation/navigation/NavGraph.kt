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
import com.nextread.readpick.presentation.chatbot.ChatBotScreen
import com.nextread.readpick.presentation.onboarding.OnboardingScreen

/**
 * ReadPick 앱의 전체 Navigation Graph
 *
 * @param navController 화면 전환을 관리하는 NavController
 * @param startDestination 앱 시작 시 표시할 화면 (기본값: Login)
 */
@Composable
fun ReadPickNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 로그인 화면
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { needsOnboarding ->
                    // 로그인 성공 후 온보딩 필요 여부에 따라 화면 전환
                    if (needsOnboarding) {
                        navController.navigate(Screen.Onboarding.route) {
                            // 로그인 화면을 백스택에서 제거 (뒤로가기 방지)
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
                    // 온보딩 완료 후 홈으로 이동
                    navController.navigate(Screen.Home.route) {
                        // 온보딩 화면을 백스택에서 제거 (뒤로가기 방지)
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // 홈 화면 (임시 Placeholder)
        composable(Screen.Home.route) {
            // TODO: 실제 HomeScreen 구현 (팀원1)
            HomeScreenPlaceholder()
        }

        composable(Screen.Chatbot.route){
            ChatbotScreen()
        }

        // TODO: 팀원들이 아래에 각자 화면 추가
        // 예시:
        // composable(Screen.BookDetail.route) { BookDetailScreen(...) }
        // composable(Screen.Search.route) { SearchScreen(...) }
        // composable(Screen.Chatbot.route) { ChatbotScreen(...) }
    }
}

/**
 * 임시 홈 화면 Placeholder
 * 팀원1이 실제 HomeScreen 구현 시 이 함수를 대체할 예정
 */
@Composable
private fun HomeScreenPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "홈 화면 (온보딩 완료!)\n\nTODO: 팀원1이 실제 HomeScreen 구현 예정")
    }
}
