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
import com.nextread.readpick.presentation.collection.components.MyLibraryContent
import com.nextread.readpick.presentation.collection.components.MyCollectionContent
import com.nextread.readpick.presentation.common.component.ReadPickBottomNavigation
import com.nextread.readpick.ui.theme.NextReadTheme
import androidx.compose.material3.TabRowDefaults // 👈 M3의 TabRowDefaults를 임포트
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset // 👈 M3의 tabIndicatorOffset을 임포트


/**
 * 내 서재 탭 정의
 *
 * - MY_LIBRARY: 즐겨찾기한 모든 책을 보여주는 탭
 * - MY_BOOKSHELF: 사용자가 직접 만든 컬렉션(책장) 목록을 보여주는 탭
 */
enum class CollectionTab(val title: String) {
    MY_LIBRARY("내 서재"),      // 즐겨찾기한 모든 책
    MY_BOOKSHELF("내 책장")     // 사용자 정의 컬렉션
}

/**
 * 내 서재 메인 화면
 *
 * 두 가지 탭으로 구성:
 * 1. 내 서재 탭: 사용자가 즐겨찾기한 모든 책을 그리드로 표시
 * 2. 내 책장 탭: 사용자가 직접 만든 컬렉션 목록 표시
 *
 * @param onNavigateToHome 홈 화면으로 이동
 * @param onNavigateToCollection 현재 화면 (내 서재) - 하단 네비게이션용
 * @param onCommunityClick 커뮤니티 화면으로 이동
 * @param onNavigateToMyPage 마이페이지로 이동
 * @param onNavigateToSearch 검색 화면으로 이동
 * @param onNavigateToCollectionCreate 새 컬렉션(책장) 만들기 화면으로 이동
 * @param onNavigateToCollectionDetail 컬렉션 상세 화면으로 이동
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionScreen(
    viewModel: CollectionViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToCollection: () -> Unit,
    onCommunityClick: () -> Unit,
    onNavigateToMyPage: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToCollectionCreate: () -> Unit,
    onNavigateToCollectionDetail: (collectionId: Long, collectionName: String) -> Unit
) {
    // 현재 선택된 탭 상태
    var selectedTab by remember { mutableStateOf(CollectionTab.MY_LIBRARY) }

    // ViewModel 상태 관찰
    val uiState by viewModel.uiState.collectAsState()

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
                onCommunityClick = onCommunityClick,
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

            // 탭별 컨텐츠
            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    CollectionTab.MY_LIBRARY -> {
                        // 탭 1: 내 서재 - 즐겨찾기한 모든 책을 그리드로 표시
                        MyLibraryContent(
                            bookCount = uiState.favoriteBookCount,
                            onFilterClick = {
                                // TODO: 필터 기능 구현 (장르별, 읽은 책/읽지 않은 책 등)
                            },
                            onEditClick = {
                                // TODO: 편집 모드 진입 (즐겨찾기 해제, 컬렉션에 추가 등)
                            },
                            onDeleteBooks = { isbn13List ->
                                viewModel.deleteFavoriteBooks(isbn13List)
                            }
                        )
                    }
                    CollectionTab.MY_BOOKSHELF -> {
                        // 탭 2: 내 책장 - 사용자가 만든 컬렉션 목록 표시
                        MyCollectionContent(
                            hasCustomCollections = uiState.hasCustomCollections,
                            collections = uiState.userCollections,
                            onMakeCollectionClick = onNavigateToCollectionCreate,
                            onEditClick = {
                                // TODO: 컬렉션 편집 모드 (삭제, 순서 변경 등)
                            },
                            onCollectionClick = onNavigateToCollectionDetail,
                            onDeleteCollections = { collectionIds ->
                                viewModel.deleteCollections(collectionIds)
                            },
                            onRenameCollection = { collectionId, newName ->
                                viewModel.renameCollection(collectionId, newName)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 내 서재 상단 앱 바
 *
 * - 제목: "내 서재"
 * - 액션: 검색 버튼 (내 서재 내에서 책 검색)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionTopBar(onSearchClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "내 서재",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "내 서재에서 책 검색"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

/**
 * 내 서재 탭 바
 *
 * "내 서재" 탭과 "내 책장" 탭을 전환할 수 있는 탭 바
 */
@Composable
fun CollectionTabBar(
    selectedTab: CollectionTab,
    onTabSelected: (CollectionTab) -> Unit
) {
    val tabs = CollectionTab.values()
    Column {
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            modifier = Modifier.padding(horizontal = 16.dp),
            containerColor = MaterialTheme.colorScheme.background,
            indicator = { tabPositions ->
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
                    text = {
                        Text(
                            text = tab.title,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Divider()
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
            onCommunityClick = {},
            onNavigateToCollectionDetail = { _, _ -> }
        )
    }
}