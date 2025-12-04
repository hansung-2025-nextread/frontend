package com.nextread.readpick.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nextread.readpick.presentation.admin.AdminDashboardScreen
import com.nextread.readpick.presentation.auth.login.LoginScreen
import com.nextread.readpick.presentation.home.HomeScreen
import com.nextread.readpick.presentation.onboarding.OnboardingScreen

// 🚨 [추가] SearchScreen import
import com.nextread.readpick.presentation.search.SearchScreen
import com.nextread.readpick.presentation.mypage.MyPageScreen

// 커뮤니티 관련 Screen import
import com.nextread.readpick.presentation.community.main.CommunityScreen
import com.nextread.readpick.presentation.community.detail.PostDetailScreen
import com.nextread.readpick.presentation.community.write.WritePostScreen
import com.nextread.readpick.presentation.community.profile.UserProfileScreen

// 내 서재 Import
import com.nextread.readpick.presentation.collection.CollectionScreen // 🚨 CollectionScreen 추가
import com.nextread.readpick.presentation.collection.CollectionCreateScreen // 🚨 CollectionCreateScreen 추가
import com.nextread.readpick.presentation.collection.CollectionSelectBookScreen // 🚨 CollectionSelectBookScreen 추가

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
                onMenuClick = { /* TODO: 네비게이션 드로어 열기 */ },

                // 🚨 [연결] 검색 화면으로 이동
                onSearchClick = {
                    navController.navigate(Screen.Search.route)
                },
                // 🚨 [연결] 챗봇 화면으로 이동 (Placeholder)
                onChatbotClick = {
                    navController.navigate(Screen.Chatbot.route)
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
        composable(Screen.Search.route) {
            SearchScreen(
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
        // 🚨 5. 내 서재 (MyLibrary / Collection Screen)
        // --------------------------------------------------------
        composable(Screen.MyLibrary.route) { backStackEntry ->
            // CollectionViewModel을 NavBackStackEntry 범위로 얻어서
            // CollectionSelectBookScreen에서도 같은 ViewModel 인스턴스를 공유할 수 있도록 함
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.MyLibrary.route)
            }

            CollectionScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToCollection = { /* 현재 화면 */ },
                onNavigateToMyPage = {
                    navController.navigate(Screen.MyPage.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                // 컬렉션 만들기 1단계로 이동
                onNavigateToCollectionCreate = {
                    navController.navigate(Screen.CollectionCreate.route)
                },
                onCommunityClick = {},
                // 컬렉션 상세로 이동
                onNavigateToCollectionDetail = { collectionId ->
                    navController.navigate(Screen.CollectionDetail.createRoute(collectionId))
                }
            )
        }

        // --------------------------------------------------------
        // 🚨 6. 컬렉션 만들기 1단계 (이름 입력)
        // --------------------------------------------------------
        composable(Screen.CollectionCreate.route) {
            CollectionCreateScreen(
                onDismiss = { navController.popBackStack() },
                // 2단계 (도서 선택) 화면으로 이동
                onNext = { name ->
                    navController.navigate(Screen.CollectionSelectBook.createRoute(name))
                }
            )
        }

        // --------------------------------------------------------
        // 🚨 7. 컬렉션 만들기 2단계 (도서 선택)
        // --------------------------------------------------------
        composable(Screen.CollectionSelectBook.route) { backStackEntry ->
            val collectionName = backStackEntry.arguments?.getString("collectionName") ?: "새 책장"

            // 부모 화면(CollectionScreen)의 NavBackStackEntry를 얻어서 ViewModel 공유
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.MyLibrary.route)
            }

            CollectionSelectBookScreen(
                collectionName = collectionName,
                parentEntry = parentEntry,
                onDismiss = { navController.popBackStack() },
                // 완료 시 내 서재 메인 화면으로 복귀
                onComplete = {
                    navController.popBackStack(Screen.MyLibrary.route, inclusive = false)
                }
            )
        }

        // --------------------------------------------------------
        // 🚨 5. 마이페이지 (MyPage Screen) - 구현된 화면으로 교체
        // --------------------------------------------------------
        composable(Screen.MyPage.route) {
            MyPageScreen(
                // 메뉴: 로그아웃 성공 시 로그인 화면으로 이동
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true } // 홈 화면까지 모두 제거
                    }
                },
                // 메뉴: 내가 작성한 리뷰 보기 화면으로 이동
                onNavigateToReviews = {
                    navController.navigate(Screen.Review.route)
                },
                // BottomNav: 홈 화면으로 이동
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false } // Home으로 돌아가기
                        launchSingleTop = true
                    }
                },
                // BottomNav: 내 서재 화면으로 이동
                onNavigateToCollection = {
                    navController.navigate(Screen.MyLibrary.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                // BottomNav: 현재 마이페이지를 다시 클릭
                onNavigateToMyPage = {
                    navController.navigate(Screen.MyPage.route) {
                        launchSingleTop = true // 현재 화면이므로 싱글 탑으로 중복 쌓임 방지
                    }
                }
            )
        }

        // --------------------------------------------------------
        // 🚨 5. 기타 화면들 (Placeholder - 임시 화면)
        // 아직 구현되지 않은 화면을 클릭해도 앱이 죽지 않게 막아줍니다.
        // --------------------------------------------------------

        // 도서 상세 (파라미터 받기 예시)
        composable(Screen.BookDetail.route) { backStackEntry ->
            val isbn13 = backStackEntry.arguments?.getString("isbn13") ?: ""
            PlaceholderScreen(name = "도서 상세 화면\nISBN: $isbn13")
        }

        // 챗봇
        composable(Screen.Chatbot.route) {
            PlaceholderScreen(name = "챗봇 화면 (구현 예정)")
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

        // --------------------------------------------------------
        // 컬렉션 상세 화면
        // --------------------------------------------------------
        composable(Screen.CollectionDetail.route) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getString("collectionId")?.toLongOrNull() ?: 0L
            PlaceholderScreen(name = "컬렉션 상세 화면\nCollection ID: $collectionId\n(구현 예정)")
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