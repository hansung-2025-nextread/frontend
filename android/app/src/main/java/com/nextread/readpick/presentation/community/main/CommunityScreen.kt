package com.nextread.readpick.presentation.community.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.nextread.readpick.data.model.community.CommunityCategoryDto
import com.nextread.readpick.data.model.community.CommunityPostDto
import com.nextread.readpick.presentation.common.component.ErrorMessage
import com.nextread.readpick.presentation.common.component.LoadingIndicator
import com.nextread.readpick.presentation.common.component.ReadPickBottomNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    onPostClick: (Long) -> Unit,
    onWriteClick: () -> Unit,
    onUserClick: (Long) -> Unit,
    onBookClick: (String) -> Unit,
    onHomeClick: () -> Unit,
    onMyLibraryClick: () -> Unit,
    onMyPageClick: () -> Unit,
    viewModel: CommunityViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    // 화면이 다시 표시될 때 데이터 새로고침
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.refresh()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("커뮤니티") }
            )
        },
        bottomBar = {
            ReadPickBottomNavigation(
                currentRoute = "community",
                onHomeClick = onHomeClick,
                onMyLibraryClick = onMyLibraryClick,
                onCommunityClick = { /* 현재 화면 */ },
                onMyPageClick = onMyPageClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onWriteClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "글쓰기"
                )
            }
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is CommunityUiState.Loading -> {
                LoadingIndicator()
            }
            is CommunityUiState.Success -> {
                CommunityContent(
                    modifier = Modifier.padding(paddingValues),
                    state = state,
                    onCategorySelect = viewModel::selectCategory,
                    onSortChange = viewModel::changeSortType,
                    onPostClick = onPostClick,
                    onUserClick = onUserClick,
                    onBookClick = onBookClick,
                    onLoadMore = viewModel::loadMorePosts
                )
            }
            is CommunityUiState.Error -> {
                ErrorMessage(
                    message = state.message,
                    onRetry = viewModel::refresh
                )
            }
        }
    }
}

@Composable
private fun CommunityContent(
    modifier: Modifier = Modifier,
    state: CommunityUiState.Success,
    onCategorySelect: (Long?) -> Unit,
    onSortChange: (SortType) -> Unit,
    onPostClick: (Long) -> Unit,
    onUserClick: (Long) -> Unit,
    onBookClick: (String) -> Unit,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyListState()

    // 무한 스크롤 감지
    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItem >= totalItems - 3
        }.collect { shouldLoadMore ->
            if (shouldLoadMore && state.hasMoreData && !state.isLoadingMore) {
                onLoadMore()
            }
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState
    ) {
        // 카테고리 탭
        item {
            CategoryTabs(
                categories = state.categories,
                selectedCategoryId = state.selectedCategoryId,
                onCategorySelect = onCategorySelect
            )
        }

        // 정렬 옵션
        item {
            SortOptions(
                selectedSortType = state.sortType,
                onSortChange = onSortChange
            )
        }

        // 게시물 목록
        items(
            items = state.posts,
            key = { it.id }
        ) { post ->
            PostCard(
                post = post,
                onClick = { onPostClick(post.id) },
                onUserClick = { onUserClick(post.authorId) },
                onBookClick = { post.bookIsbn13?.let { onBookClick(it) } }
            )
        }

        // 로딩 더보기 인디케이터
        if (state.isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@Composable
private fun CategoryTabs(
    categories: List<CommunityCategoryDto>,
    selectedCategoryId: Long?,
    onCategorySelect: (Long?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 전체 탭
        FilterChip(
            selected = selectedCategoryId == null,
            onClick = { onCategorySelect(null) },
            label = { Text("전체") }
        )

        // 카테고리 탭들
        categories.forEach { category ->
            FilterChip(
                selected = selectedCategoryId == category.id,
                onClick = { onCategorySelect(category.id) },
                label = { Text(category.name) }
            )
        }
    }
}

@Composable
private fun SortOptions(
    selectedSortType: SortType,
    onSortChange: (SortType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SortType.entries.forEach { sortType ->
            FilterChip(
                selected = selectedSortType == sortType,
                onClick = { onSortChange(sortType) },
                label = { Text(sortType.displayName) }
            )
        }
    }
}

@Composable
private fun PostCard(
    post: CommunityPostDto,
    onClick: () -> Unit,
    onUserClick: () -> Unit,
    onBookClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 헤더: 카테고리, 작성자, 시간
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 카테고리 배지
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = post.categoryName,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 작성자
                Text(
                    text = post.authorName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.clickable(onClick = onUserClick)
                )

                Text(
                    text = " · ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // 시간
                Text(
                    text = formatRelativeTime(post.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 게시물 타입 + 제목
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "[${getPostTypeDisplayName(post.postType)}]",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 내용 미리보기
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // 연결된 책 (있으면)
            if (post.bookIsbn13 != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable(onClick = onBookClick)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = post.bookCover,
                        contentDescription = post.bookTitle,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = post.bookTitle ?: "",
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 좋아요, 댓글 수
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (post.liked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "좋아요",
                    modifier = Modifier.size(16.dp),
                    tint = if (post.liked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${post.likeCount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "💬 ${post.commentCount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun getPostTypeDisplayName(postType: String): String {
    return when (postType) {
        "REVIEW" -> "후기"
        "QUESTION" -> "질문"
        "DISCUSSION" -> "자유토론"
        else -> postType
    }
}

private fun formatRelativeTime(createdAt: String): String {
    // 간단한 상대 시간 표시 (실제로는 더 정교한 로직 필요)
    // ISO 8601 형식 파싱 후 현재 시간과 비교
    return try {
        // 임시로 그대로 표시 (나중에 개선)
        createdAt.take(10)
    } catch (e: Exception) {
        createdAt
    }
}
