package com.nextread.readpick.presentation.home

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.nextread.readpick.R
import com.nextread.readpick.data.model.book.BookDto
import com.nextread.readpick.presentation.common.component.ReadPickBottomNavigation

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
    val personalizedBooks by viewModel.personalizedBooks.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()

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
                personalizedBooks = personalizedBooks,
                userName = userName,
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
    books: List<BookDto>, // 베스트셀러 목록
    personalizedBooks: List<BookDto> = emptyList(), // 개인화 추천 목록
    userName: String? = null, // 사용자 이름
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
            ReadPickBottomNavigation(
                currentRoute = "home",
                onHomeClick = { /* 현재 화면 */ },
                onMyLibraryClick = onMyLibraryClick,
                onCommunityClick = onCommunityClick,
                onMyPageClick = onMyPageClick
            )
        },
        floatingActionButton = {
            ChatbotFab(onClick = onChatbotClick)
        },
        floatingActionButtonPosition = FabPosition.End,
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        // Scaffold 내부 컨텐츠
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()) // 스크롤 추가
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // 1. 검색창
            SearchTriggerBar(
                onClick = onSearchClick
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 2. 베스트셀러 목록 (API 연동)
            BestsellerSection(
                books = books,
                onBookClick = onBookClick
            )

            // 3. 개인화 추천 목록
            if (personalizedBooks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(32.dp))
                PersonalizedRecommendationSection(
                    books = personalizedBooks,
                    userName = userName,
                    onBookClick = onBookClick
                )
            }
        }
    }
}

/**
 * 개인화 추천도서 목록 (LazyRow)
 */
@Composable
fun PersonalizedRecommendationSection(
    books: List<BookDto>,
    userName: String?,
    onBookClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // 제목
        val title = if (!userName.isNullOrEmpty()) {
            "$userName 님을 위한 추천도서"
        } else {
            "추천도서"
        }

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 책 목록
        BoxWithConstraints {
            val itemWidth = maxWidth / 2.5f
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(books) { book ->
                    BookCoverItem(
                        modifier = Modifier.width(itemWidth),
                        book = book,
                        onBookClick = onBookClick
                    )
                }
            }
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
            text = "베스트셀러",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (books.isEmpty()) {
            // 책이 없을 경우
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "베스트셀러 정보가 없습니다.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // 책 목록을 가로 스크롤로 표시
            BoxWithConstraints {
                val itemWidth = maxWidth / 2.5f
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(books) { book ->
                        BookCoverItem(
                            modifier = Modifier.width(itemWidth),
                            book = book,
                            onBookClick = onBookClick
                        )
                    }
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
    modifier: Modifier = Modifier,
    book: BookDto,
    onBookClick: (String) -> Unit
) {
    Column(
        modifier = modifier
            .clickable { onBookClick(book.isbn13) },
        horizontalAlignment = Alignment.Start
    ) {
        // 책 표지 카드
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp,
                pressedElevation = 2.dp
            )
        ) {
            Box {
                // Coil 라이브러리를 사용해 URL 이미지 로드
                AsyncImage(
                    model = book.cover,
                    contentDescription = book.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.ic_menu),
                    error = painterResource(id = R.drawable.ic_menu)
                )
                // 하단 그라데이션 오버레이
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.3f)
                                )
                            )
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 책 제목
        Text(
            text = book.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onBackground,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // 저자
        Text(
            text = book.author,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
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
            .padding(top = 30.dp, start = 20.dp, end = 20.dp, bottom = 12.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Next Read",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
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
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "검색",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = "도서를 검색하세요",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.weight(1f)
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
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp
        )
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_chatbot),
            contentDescription = "챗봇",
            tint = Color.Unspecified,
            modifier = Modifier.size(36.dp)
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
            .padding(horizontal = 20.dp, vertical = 12.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
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
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else Color.Transparent
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painter,
            contentDescription = label,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 11.sp
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

    val dummyPersonalizedBooks = listOf(
        BookDto("111", "개인화 추천 1", "저자A", "", "설명...", "소설"),
        BookDto("222", "개인화 추천 2", "저자B", "", "설명...", "에세이"),
        BookDto("333", "개인화 추천 3", "저자C", "", "설명...", "자기계발")
    )

    MaterialTheme {
        // UI Content 함수를 임시 데이터로 호출
        HomeScreenContent(
            books = dummyBooks,
            personalizedBooks = dummyPersonalizedBooks,
            userName = "홍길동",
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