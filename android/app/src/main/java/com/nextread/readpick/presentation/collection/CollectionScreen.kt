package com.nextread.readpick.presentation.collection

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nextread.readpick.presentation.collection.components.BaseShelfContent
import com.nextread.readpick.presentation.collection.components.MyCollectionContent
import com.nextread.readpick.presentation.common.component.ReadPickBottomNavigation
import com.nextread.readpick.ui.theme.NextReadTheme
import androidx.compose.material3.TabRowDefaults // 👈 M3의 TabRowDefaults를 임포트
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset // 👈 M3의 tabIndicatorOffset을 임포트


// 탭 정의
enum class CollectionTab(val title: String) {
    BASE_SHELF("기본책장"),
    MY_COLLECTION("내 책장")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionScreen(
    // viewModel: CollectionViewModel = hiltViewModel(), // ViewModel 연동 시 사용
    onNavigateToHome: () -> Unit,
    onNavigateToCollection: () -> Unit,
    onNavigateToMyPage: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToCollectionCreate: () -> Unit, // 새 컬렉션 생성 화면 이동
    onNavigateToCollectionDetail: (collectionId: Long) -> Unit, // 컬렉션 상세 화면 이동
) {
    // 현재 선택된 탭 상태
    var selectedTab by remember { mutableStateOf(CollectionTab.BASE_SHELF) }

    // 임시 데이터 (ViewModel에서 로드 예정)
    val hasCustomCollections = remember { mutableStateOf(true) } // 내 책장이 있는지 여부 (테스트용)

    Scaffold(
        topBar = {
            CollectionTopBar(
                onSearchClick = onNavigateToSearch
            )
        },
        bottomBar = {
            ReadPickBottomNavigation(
                currentRoute = "mylibrary",
                onHomeClick = onNavigateToHome,
                onMyLibraryClick = onNavigateToCollection, // 현재 화면
                onMyPageClick = onNavigateToMyPage
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 탭 바
            CollectionTabBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            // 탭 내용
            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    CollectionTab.BASE_SHELF -> {
                        // 기본 책장 (즐겨찾기 책 목록)
                        BaseShelfContent(
                            bookCount = 1, // 임시 데이터
                            onFilterClick = { /* 필터 액션 */ },
                            onEditClick = { /* 편집 액션 */ }
                        )
                    }
                    CollectionTab.MY_COLLECTION -> {
                        // 내 책장 (사용자 정의 컬렉션 목록)
                        MyCollectionContent(
                            hasCustomCollections = hasCustomCollections.value,
                            onMakeCollectionClick = onNavigateToCollectionCreate,
                            onEditClick = { /* 편집 액션 */ },
                            onCollectionClick = onNavigateToCollectionDetail // 상세 이동
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionTopBar(onSearchClick: () -> Unit) {
    TopAppBar(
        title = {
            Text("내 서재", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "검색"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
fun CollectionTabBar(
    selectedTab: CollectionTab,
    onTabSelected: (CollectionTab) -> Unit
) {
    val tabs = CollectionTab.values()
    Column {
        TabRow(
            selectedTabIndex = selectedTab.ordinal, // indexOf 대신 ordinal 사용 (더 효율적)
            modifier = Modifier.padding(horizontal = 16.dp),
            containerColor = MaterialTheme.colorScheme.background,
            indicator = { tabPositions ->
                // 🚨🚨🚨 M3의 PrimaryIndicator를 사용하되, M2의 tabIndicatorOffset을 Modifier에 적용
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                    height = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            tabs.forEach { tab ->
                Tab(
                    selected = tab == selectedTab,
                    onClick = { onTabSelected(tab) },
                    text = { Text(tab.title, fontWeight = FontWeight.SemiBold) },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Divider() // Divider는 material3에서 가져온 것을 사용합니다.
    }
}


@Preview(showBackground = true)
@Composable
fun CollectionScreenPreview() {
    NextReadTheme {
        CollectionScreen(
            onNavigateToHome = {},
            onNavigateToCollection = {},
            onNavigateToMyPage = {},
            onNavigateToSearch = {},
            onNavigateToCollectionCreate = {},
            onNavigateToCollectionDetail = { _ -> }
        )
    }
}