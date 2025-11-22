package com.nextread.readpick.presentation.home


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.nextread.readpick.R
import com.nextread.readpick.data.model.book.BookDto

/**
 * 메인 HomeScreen Composable (ViewModel과 상태 관리)
 *
 * NavGraph에서 이 함수를 호출합니다.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(), // Hilt가 ViewModel 주입
    onSearchClick: () -> Unit,
    onMenuClick: () -> Unit,
    onChatbotClick: () -> Unit,
    onMyLibraryClick: () -> Unit,
    onCommunityClick: () -> Unit,
    onMyPageClick: () -> Unit,
    onBookClick: (String) -> Unit // 책 클릭 시 ISBN13 전달
) {
    // ViewModel의 uiState를 관찰
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // uiState (Sealed Interface)에 따라 UI 분기
    when (val state = uiState) {
        is HomeUiState.Loading -> {
            // 로딩 중: 화면 중앙에 로딩 스피너 표시
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is HomeUiState.Error -> {
            // 에러 발생: 화면 중앙에 에러 메시지 표시
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "오류: ${state.message}")
            }
        }
        is HomeUiState.Success -> {
            // 성공: state.books 데이터를 UI Content에 전달
            HomeScreenContent(
                books = state.books,
                onSearchClick = onSearchClick,
                onMenuClick = onMenuClick,
                onChatbotClick = onChatbotClick,
                onMyLibraryClick = onMyLibraryClick,
                onCommunityClick = onCommunityClick,
                onMyPageClick = onMyPageClick,
                onBookClick = onBookClick
            )
        }
    }
}

/**
 * 실제 UI 로직 (Scaffold와 내부 구성 요소)
 *
 * Success 상태일 때 HomeScreen에서 호출됩니다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    books: List<BookDto>, // API로부터 받은 책 목록
    onSearchClick: () -> Unit,
    onMenuClick: () -> Unit,
    onChatbotClick: () -> Unit,
    onMyLibraryClick: () -> Unit,
    onCommunityClick: () -> Unit,
    onMyPageClick: () -> Unit,
    onBookClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            HomeTopBar(
                onMenuClick = onMenuClick
            )
        },
        bottomBar = {
            HomeBottomNavigation(
                onHomeClick = { /* 현재 화면 */ },
                onMyLibraryClick = onMyLibraryClick,
                onCommunityClick = onCommunityClick,
                onMyPageClick = onMyPageClick
            )
        },
        floatingActionButton = {
            ChatbotFab(onClick = onChatbotClick)
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        // Scaffold 내부 컨텐츠
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. 검색창
            SearchTriggerBar(
                onClick = onSearchClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. 베스트셀러 목록 (API 연동)
            BestsellerSection(
                books = books,
                onBookClick = onBookClick
            )
        }
    }
}

/**
 * 베스트셀러 목록 (LazyRow)
 */
@Composable
fun BestsellerSection(
    books: List<BookDto>,
    onBookClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "베스트셀러", // 시안의 "추천 도서" -> "베스트셀러"
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (books.isEmpty()) {
            // 책이 없을 경우
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "베스트셀러 정보가 없습니다.")
            }
        } else {
            // 책 목록을 가로 스크롤로 표시
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(books) { book ->
                    BookCoverItem(
                        book = book,
                        onBookClick = onBookClick
                    )
                }
            }
        }
    }
}

/**
 * 개별 책 아이템 (Coil 이미지 로더 사용)
 */
@Composable
fun BookCoverItem(
    book: BookDto,
    onBookClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable { onBookClick(book.isbn13) }, // 클릭 시 isbn13 전달
        horizontalAlignment = Alignment.Start
    ) {
        // Coil 라이브러리를 사용해 URL 이미지 로드
        AsyncImage(
            model = book.cover, // API에서 받은 책 표지 URL
            contentDescription = book.title,
            modifier = Modifier
                .width(120.dp)
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop,
            // TODO: 로딩/에러 시 보여줄 기본 이미지를 drawable에 추가하고 연결
            placeholder = painterResource(id = R.drawable.ic_menu),
            error = painterResource(id = R.drawable.ic_menu)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = book.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis // 제목이 길면 ... 처리
        )
        Text(
            text = book.author,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis, // 저자가 길면 ... 처리
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * 상단 앱 바
 */
@Composable
fun HomeTopBar(
    onMenuClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(30.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onMenuClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_menu),
                    contentDescription = "메뉴",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = "Next Read",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.size(48.dp)) // 오른쪽 정렬을 위한 공간
        }
    }
}

/**
 * 검색창 (클릭 시 이동)
 */
@Composable
fun SearchTriggerBar(
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(30.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "도서를 검색하세요",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "검색",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 챗봇 FAB (Floating Action Button)
 */
@Composable
fun ChatbotFab(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_chatbot),
            contentDescription = "챗봇",
            tint = Color.Unspecified, // 이미지 원본 색상 사용
            modifier = Modifier.size(35.dp) // 아이콘 크기 조절
        )
    }
}

/**
 * 하단 네비게이션 바
 */
@Composable
fun HomeBottomNavigation(
    onHomeClick: () -> Unit,
    onMyLibraryClick: () -> Unit,
    onCommunityClick: () -> Unit,
    onMyPageClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(30.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                label = "홈",
                painter = painterResource(id = R.drawable.ic_home),
                isSelected = true,
                onClick = onHomeClick
            )
            BottomNavItem(
                label = "내 서재",
                painter = painterResource(id = R.drawable.ic_library),
                isSelected = false,
                onClick = onMyLibraryClick
            )
            BottomNavItem(
                label = "커뮤니티",
                painter = painterResource(id = R.drawable.ic_community),
                isSelected = false,
                onClick = onCommunityClick
            )
            BottomNavItem(
                label = "마이페이지",
                painter = painterResource(id = R.drawable.ic_mypage),
                isSelected = false,
                onClick = onMyPageClick
            )
        }
    }
}

/**
 * 하단 네비게이션 아이템 (Painter - drawable 리소스 사용)
 */
@Composable
fun BottomNavItem(
    label: String,
    painter: Painter,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // 👈 Crash 수정된 부분
    ) {
        Icon(
            painter = painter,
            contentDescription = label,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}


/**
 * UI 확인용 Preview
 */
@Preview(showBackground = true, device = "id:pixel_6")
@Composable
fun HomeScreenPreview() {
    // Preview에서 사용할 임시 책 데이터
    val dummyBooks = listOf(
        BookDto("123", "Compose 마스터하기", "팀원1", "", "설명...", "IT"),
        BookDto("456", "API 연동의 모든 것", "팀원1", "", "설명...", "IT"),
        BookDto("789", "Jetpack 최고", "팀원1", "", "설명...", "IT")
    )

    MaterialTheme {
        // UI Content 함수를 임시 데이터로 호출
        HomeScreenContent(
            books = dummyBooks,
            onSearchClick = {},
            onMenuClick = {},
            onChatbotClick = {},
            onMyLibraryClick = {},
            onCommunityClick = {},
            onMyPageClick = {},
            onBookClick = {}
        )
    }
}