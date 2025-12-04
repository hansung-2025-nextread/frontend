package com.nextread.readpick.presentation.collection.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nextread.readpick.R
import com.nextread.readpick.data.model.collection.CollectionBookResponse

/**
 * 컬렉션 상세 화면
 *
 * 컬렉션에 담긴 책 목록을 보여주고,
 * 책 추가/삭제 기능을 제공합니다.
 *
 * @param collectionId 컬렉션 ID
 * @param collectionName 컬렉션 이름
 * @param onBackClick 뒤로가기 클릭
 * @param onAddBookClick 책 추가 버튼 클릭
 * @param onBookClick 책 클릭 시 상세 화면으로 이동
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionDetailScreen(
    collectionId: Long,
    collectionName: String,
    onBackClick: () -> Unit,
    onAddBookClick: () -> Unit,
    onBookClick: (String) -> Unit,
    viewModel: CollectionDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 컬렉션 이름 설정
    LaunchedEffect(collectionName) {
        viewModel.setCollectionName(collectionName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.collectionName.ifEmpty { "내 책장" },
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                actions = {
                    // 편집 버튼
                    TextButton(onClick = { viewModel.toggleEditMode() }) {
                        Text(
                            text = if (uiState.isEditMode) "완료" else "편집",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            // 편집 모드가 아닐 때만 FAB 표시
            if (!uiState.isEditMode) {
                FloatingActionButton(
                    onClick = onAddBookClick,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "책 추가")
                }
            }
        },
        bottomBar = {
            // 편집 모드일 때 삭제 버튼 표시
            if (uiState.isEditMode && uiState.selectedCount > 0) {
                Button(
                    onClick = { viewModel.deleteSelectedBooks() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("${uiState.selectedCount}권 삭제")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.error ?: "오류가 발생했습니다",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { /* Retry */ }) {
                            Text("다시 시도")
                        }
                    }
                }
                !uiState.hasBooks -> {
                    // 빈 상태
                    EmptyBooksState(
                        onAddBookClick = onAddBookClick,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    // 책 목록
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.books) { book ->
                            BookGridItem(
                                book = book,
                                isEditMode = uiState.isEditMode,
                                isSelected = book.isbn13 in uiState.selectedBookIds,
                                onBookClick = {
                                    if (uiState.isEditMode) {
                                        viewModel.toggleBookSelection(book.isbn13)
                                    } else {
                                        onBookClick(book.isbn13)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 빈 상태 컴포저블
 */
@Composable
fun EmptyBooksState(
    onAddBookClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "📚",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "책장이 비어있습니다",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "책을 추가해보세요",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAddBookClick,
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("책 추가하기")
        }
    }
}

/**
 * 책 그리드 아이템
 */
@Composable
fun BookGridItem(
    book: CollectionBookResponse,
    isEditMode: Boolean,
    isSelected: Boolean,
    onBookClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onBookClick)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 책 표지
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.67f) // 책 비율
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                // TODO: Coil로 실제 이미지 로드
                Image(
                    painter = painterResource(id = R.drawable.ic_menu),
                    contentDescription = book.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // 편집 모드일 때 체크박스
                if (isEditMode) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                if (isSelected) Color.Black.copy(alpha = 0.3f)
                                else Color.Transparent
                            )
                    )

                    // 체크 아이콘
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "선택됨",
                            tint = Color.White,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(48.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 책 제목
            Text(
                text = book.title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            // 저자
            Text(
                text = book.author,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}