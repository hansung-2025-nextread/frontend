package com.nextread.readpick.presentation.mypage

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nextread.readpick.presentation.mypage.components.MyPageHeader
import com.nextread.readpick.presentation.mypage.components.MyPageMenuItem
import com.nextread.readpick.ui.theme.NextReadTheme
import com.nextread.readpick.presentation.common.component.ReadPickBottomNavigation

// -------------------------------------------------------------------------------------------------
// 1. MyPageScreen: ViewModel 주입 및 상태 관리 (NavGraph에서 호출)
// -------------------------------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageScreen(
    viewModel: MyPageViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onNavigateToReviews: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToCollection: () -> Unit,
    onNavigateToMyPage: () -> Unit
) {
    // 💡 ViewModel 상태 관찰 로직을 여기에 추가합니다.
    val uiState by viewModel.uiState.collectAsState()

    // 🚨🚨🚨 [최종 해결 로직] isLoggedOut 상태 변화를 LaunchedEffect로 감지 🚨🚨🚨
    // 네비게이션과 상태 초기화는 이 블록에서만 실행되어야 합니다.
    LaunchedEffect(key1 = uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            // 1. 네비게이션 실행 (로그인 화면으로 이동)
            onNavigateToLogin()

            // 2. ViewModel 상태 초기화 (매우 중요! 중복 호출 및 무한 루프 방지)
            // 네비게이션이 완료된 후 Composable이 재구성될 때, 이 플래그를 false로 되돌립니다.
            viewModel.resetLogoutState()
        }
    }

    // 데이터 로딩 중이거나 오류 발생 시 처리
    if (uiState.isLoading) {
        // TODO: 로딩 인디케이터 표시
    }

    // ViewModel에서 로드한 데이터 사용 (로컬 또는 API)
    val userName = uiState.userInfo?.name ?: "이름"
    val userEmail = uiState.userInfo?.email ?: "이메일"
    val profileImageUrl = uiState.userInfo?.profileImageUrl

    MyPageContent(
        userName = userName,
        userEmail = userEmail,
        profileImageUrl = profileImageUrl,
        onLogoutClick = viewModel::onLogoutClick,
        onReviewsClick = onNavigateToReviews,
        onNavigateToHome = onNavigateToHome,
        onNavigateToCollection = onNavigateToCollection,
        onCommunityClick = {},
        onNavigateToMyPage = onNavigateToMyPage
    )
}

// -------------------------------------------------------------------------------------------------
// 2. MyPageContent: 순수 UI 렌더링 로직 (ViewModel 의존성 제거)
// -------------------------------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyPageContent(
    userName: String,
    userEmail: String,
    profileImageUrl: String?,
    onLogoutClick: () -> Unit,
    onReviewsClick: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToCollection: () -> Unit,
    onCommunityClick: () -> Unit,
    onNavigateToMyPage: () -> Unit
) {
    Scaffold(
        topBar = {
            MyPageTopBar()
        },
        bottomBar = {
            ReadPickBottomNavigation(
                currentRoute = "mypage",
                onHomeClick = onNavigateToHome,
                onMyLibraryClick = onNavigateToCollection,
                onCommunityClick = onCommunityClick,
                onMyPageClick = onNavigateToMyPage
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 사용자 정보 섹션
            MyPageHeader(
                name = userName,
                email = userEmail,
                profileImageUrl = profileImageUrl,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Divider(thickness = 0.5.dp)

            // 메뉴 목록 섹션
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 로그아웃
                MyPageMenuItem(text = "로그아웃", onClick = onLogoutClick)
                Divider(thickness = 0.5.dp)

                // 내가 작성한 리뷰 보기
                MyPageMenuItem(text = "내가 작성한 리뷰 보기", onClick = onReviewsClick)
                Divider(thickness = 0.5.dp)
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 3. MyPageTopBar: 상단 앱 바
// -------------------------------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageTopBar() {
    TopAppBar(
        title = { Text("Next Read") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

// -------------------------------------------------------------------------------------------------
// 4. Preview: ViewModel 주입 오류를 피하기 위해 MyPageContent 호출
// -------------------------------------------------------------------------------------------------

@Preview(showBackground = true, apiLevel = 35)
@Composable
fun MyPageScreenPreview() {
    NextReadTheme {
        MyPageContent(
            userName = "이름",
            userEmail = "이메일",
            profileImageUrl = null,
            onLogoutClick = {},
            onReviewsClick = {},
            onNavigateToHome = {},
            onCommunityClick = {},
            onNavigateToCollection = {},
            onNavigateToMyPage = {}
        )
    }
}