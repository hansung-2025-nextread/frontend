package com.nextread.readpick.presentation.community.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.nextread.readpick.data.model.community.CommunityCommentDto
import com.nextread.readpick.data.model.community.CommunityPostDto
import com.nextread.readpick.presentation.common.component.ErrorMessage
import com.nextread.readpick.presentation.common.component.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    onBackClick: () -> Unit,
    onUserClick: (Long) -> Unit,
    onBookClick: (String) -> Unit,
    viewModel: PostDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var commentText by remember { mutableStateOf("") }
    var showDeletePostDialog by remember { mutableStateOf(false) }

    // 현재 사용자 ID
    val currentUserId = viewModel.currentUserId

    // 게시글 삭제 완료 이벤트 처리
    LaunchedEffect(Unit) {
        viewModel.postDeletedEvent.collect {
            onBackClick()
        }
    }

    // 게시글 삭제 확인 다이얼로그
    if (showDeletePostDialog) {
        AlertDialog(
            onDismissRequest = { showDeletePostDialog = false },
            title = { Text("게시글 삭제") },
            text = { Text("정말로 이 게시글을 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeletePostDialog = false
                        viewModel.deletePost()
                    }
                ) {
                    Text("삭제", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeletePostDialog = false }) {
                    Text("취소")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                },
                actions = {
                    // 본인 게시글일 때만 삭제 버튼 표시
                    val state = uiState
                    if (state is PostDetailUiState.Success &&
                        currentUserId != null &&
                        state.post.authorId == currentUserId) {
                        IconButton(onClick = { showDeletePostDialog = true }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "게시글 삭제",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            when (val state = uiState) {
                is PostDetailUiState.Success -> {
                    CommentInputBar(
                        value = commentText,
                        onValueChange = { commentText = it },
                        onSend = {
                            viewModel.addComment(commentText)
                            commentText = ""
                        },
                        isLoading = state.isCommentLoading
                    )
                }
                else -> {}
            }
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is PostDetailUiState.Loading -> {
                LoadingIndicator()
            }
            is PostDetailUiState.Success -> {
                PostDetailContent(
                    modifier = Modifier.padding(paddingValues),
                    post = state.post,
                    comments = state.comments,
                    isLikeLoading = state.isLikeLoading,
                    currentUserId = currentUserId,
                    onLikeClick = viewModel::toggleLike,
                    onUserClick = onUserClick,
                    onBookClick = onBookClick,
                    onDeleteComment = viewModel::deleteComment
                )
            }
            is PostDetailUiState.Error -> {
                ErrorMessage(
                    message = state.message,
                    onRetry = viewModel::refresh
                )
            }
        }
    }
}

@Composable
private fun PostDetailContent(
    modifier: Modifier = Modifier,
    post: CommunityPostDto,
    comments: List<CommunityCommentDto>,
    isLikeLoading: Boolean,
    currentUserId: Long?,
    onLikeClick: () -> Unit,
    onUserClick: (Long) -> Unit,
    onBookClick: (String) -> Unit,
    onDeleteComment: (Long) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        // 작성자 정보
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onUserClick(post.authorId) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 프로필 이미지
                AsyncImage(
                    model = post.authorPicture,
                    contentDescription = post.authorName,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = post.authorName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = post.createdAt.take(10),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 제목
        item {
            Text(
                text = "[${getPostTypeDisplayName(post.postType)}] ${post.title}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // 내용
        item {
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }

        // 연결된 책
        if (post.bookIsbn13 != null) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onBookClick(post.bookIsbn13) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = post.bookCover,
                        contentDescription = post.bookTitle,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = post.bookTitle ?: "",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 좋아요/공유 버튼
        item {
            Column {
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "❤️ 좋아요 ${post.likeCount}  💬 댓글 ${post.commentCount}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onLikeClick,
                        enabled = !isLikeLoading,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isLikeLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (post.liked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "좋아요",
                                tint = if (post.liked) MaterialTheme.colorScheme.error else LocalContentColor.current
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("좋아요")
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            }
        }

        // 댓글 헤더
        item {
            Text(
                text = "댓글",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // 댓글 목록
        items(
            items = comments,
            key = { it.id }
        ) { comment ->
            CommentItem(
                comment = comment,
                isMyComment = currentUserId != null && comment.authorId == currentUserId,
                onUserClick = { onUserClick(comment.authorId) },
                onDeleteClick = { onDeleteComment(comment.id) }
            )
        }

        // 빈 댓글
        if (comments.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "첫 댓글을 남겨보세요!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 하단 여백
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun CommentItem(
    comment: CommunityCommentDto,
    isMyComment: Boolean,
    onUserClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    // 댓글 삭제 확인 다이얼로그
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("댓글 삭제") },
            text = { Text("정말로 이 댓글을 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteClick()
                    }
                ) {
                    Text("삭제", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("취소")
                }
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        AsyncImage(
            model = comment.authorPicture,
            contentDescription = comment.authorName,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable(onClick = onUserClick),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = comment.authorName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = onUserClick)
                )
                Text(
                    text = " · ${comment.createdAt.take(10)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodySmall
            )
        }
        // 본인 댓글일 때만 삭제 버튼 표시
        if (isMyComment) {
            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "댓글 삭제",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun CommentInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    isLoading: Boolean
) {
    Surface(
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("댓글 입력...") },
                maxLines = 3,
                enabled = !isLoading
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onSend,
                enabled = value.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "전송",
                        tint = if (value.isNotBlank()) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
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
