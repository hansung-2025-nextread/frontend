package com.nextread.readpick.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nextread.readpick.presentation.admin.AdminDashboardScreen
import com.nextread.readpick.presentation.auth.login.LoginScreen
import com.nextread.readpick.presentation.home.HomeScreen
import com.nextread.readpick.presentation.onboarding.OnboardingScreen

// 🚨 [추가] SearchScreen import
import com.nextread.readpick.presentation.search.SearchScreen

// 카테고리 선택 Screen import
import com.nextread.readpick.presentation.category.CategorySelectScreen

// 커뮤니티 관련 Screen import
import com.nextread.readpick.presentation.community.main.CommunityScreen
import com.nextread.readpick.presentation.community.detail.PostDetailScreen
import com.nextread.readpick.presentation.community.write.WritePostScreen
import com.nextread.readpick.presentation.community.profile.UserProfileScreen

// 챗봇 관련 Screen import
import com.nextread.readpick.presentation.chatbot.sessionlist.ChatbotSessionListScreen
import com.nextread.readpick.presentation.chatbot.chat.ChatScreen

/**
 * ReadPick 앱의 전체 Navigation Graph
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
        // 1. 로그인 화면
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { needsOnboarding, isAdmin ->
                    when {
                        // 관리자인 경우 AdminDashboard로 이동
                        isAdmin -> {
                            navController.navigate(Screen.AdminDashboard.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                        // 온보딩 필요한 경우
                        needsOnboarding -> {
                            navController.navigate(Screen.Onboarding.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                        // 일반 사용자 홈으로 이동
                        else -> {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        // 2. 온보딩 화면
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onOnboardingComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // 3. 홈 화면
        composable(Screen.Home.route) {
            HomeScreen(
                // 🚨 [연결] 검색 화면으로 이동
                onSearchClick = {
                    navController.navigate(Screen.Search.createRoute())
                },
                // 🚨 [연결] 카테고리 선택 화면으로 이동
                onMenuClick = {
                    navController.navigate(Screen.CategorySelect.route)
                },
                // 🚨 [연결] 챗봇 화면으로 이동
                onChatbotClick = {
                    navController.navigate(Screen.ChatbotSessionList.route)
                },
                // 🚨 [연결] 내 서재 화면으로 이동 (Placeholder)
                onMyLibraryClick = {
                    navController.navigate(Screen.MyLibrary.route)
                },
                // 🚨 [연결] 커뮤니티 화면으로 이동
                onCommunityClick = {
                    navController.navigate(Screen.Community.route)
                },
                // 🚨 [연결] 마이페이지 화면으로 이동 (Placeholder)
                onMyPageClick = {
                    navController.navigate(Screen.MyPage.route)
                },
                // 🚨 [연결] 책 상세 화면으로 이동
                onBookClick = { isbn13 ->
                    navController.navigate(Screen.BookDetail.createRoute(isbn13))
                }
            )
        }

        // --------------------------------------------------------
        // 🚨 4. 검색 화면 (SearchScreen 연결)
        // --------------------------------------------------------
        composable(
            route = Screen.Search.route,
            arguments = listOf(
                navArgument("categoryId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")
                ?.toLongOrNull()

            SearchScreen(
                categoryId = categoryId,
                // 뒤로가기 버튼 클릭 시
                onBackClick = {
                    navController.popBackStack()
                },
                // 검색 결과에서 책 클릭 시 상세 화면으로 이동
                onBookClick = { isbn13 ->
                    navController.navigate(Screen.BookDetail.createRoute(isbn13))
                }
            )
        }

        // --------------------------------------------------------
        // 🚨 5. 카테고리 선택 화면
        // --------------------------------------------------------
        composable(Screen.CategorySelect.route) {
            CategorySelectScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onCategorySelected = { categoryId ->
                    navController.navigate(Screen.Search.createRoute(categoryId))
                }
            )
        }

        // --------------------------------------------------------
        // 🚨 6. 기타 화면들 (Placeholder - 임시 화면)
        // 아직 구현되지 않은 화면을 클릭해도 앱이 죽지 않게 막아줍니다.
        // --------------------------------------------------------

        // 도서 상세 (파라미터 받기 예시)
        composable(Screen.BookDetail.route) { backStackEntry ->
            val isbn13 = backStackEntry.arguments?.getString("isbn13") ?: ""
            PlaceholderScreen(name = "도서 상세 화면\nISBN: $isbn13")
        }

        // 챗봇 세션 목록 화면
        composable(Screen.ChatbotSessionList.route) {
            ChatbotSessionListScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSessionClick = { sessionId ->
                    navController.navigate(Screen.Chat.createRoute(sessionId))
                }
            )
        }

        // 챗봇 채팅 화면
        composable(Screen.Chat.route) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId")?.toLongOrNull()
            if (sessionId != null) {
                ChatScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onBookClick = { isbn13 ->
                        navController.navigate(Screen.BookDetail.createRoute(isbn13))
                    }
                )
            } else {
                // sessionId 없으면 에러 화면
                PlaceholderScreen("세션 ID가 없습니다")
            }
        }

        // 내 서재
        composable(Screen.MyLibrary.route) {
            PlaceholderScreen(name = "내 서재 화면 (구현 예정)")
        }

        // 마이페이지
        composable(Screen.MyPage.route) {
            PlaceholderScreen(name = "마이페이지 (구현 예정)")
        }

        // 리뷰
        composable(Screen.Review.route) {
            PlaceholderScreen(name = "리뷰 화면 (구현 예정)")
        }

        // 관리자 대시보드
        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // --------------------------------------------------------
        // 커뮤니티 관련 화면들
        // --------------------------------------------------------

        // 커뮤니티 메인
        composable(Screen.Community.route) {
            CommunityScreen(
                onPostClick = { postId ->
                    navController.navigate(Screen.PostDetail.createRoute(postId))
                },
                onWriteClick = {
                    navController.navigate(Screen.WritePost.route)
                },
                onUserClick = { userId ->
                    navController.navigate(Screen.UserProfile.createRoute(userId))
                },
                onBookClick = { isbn13 ->
                    navController.navigate(Screen.BookDetail.createRoute(isbn13))
                }
            )
        }

        // 게시물 상세
        composable(Screen.PostDetail.route) {
            PostDetailScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onUserClick = { userId ->
                    navController.navigate(Screen.UserProfile.createRoute(userId))
                },
                onBookClick = { isbn13 ->
                    navController.navigate(Screen.BookDetail.createRoute(isbn13))
                }
            )
        }

        // 글쓰기
        composable(Screen.WritePost.route) {
            WritePostScreen(
                onClose = {
                    navController.popBackStack()
                },
                onPostCreated = { postId ->
                    // 글쓰기 화면을 닫고 게시물 상세로 이동
                    navController.popBackStack()
                    navController.navigate(Screen.PostDetail.createRoute(postId))
                }
            )
        }

        // 사용자 프로필
        composable(Screen.UserProfile.route) {
            UserProfileScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onPostClick = { postId ->
                    navController.navigate(Screen.PostDetail.createRoute(postId))
                }
            )
        }
    }
}

/**
 * 임시 화면 (구현되지 않은 화면용)
 */
@Composable
private fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = name)
    }
}