package com.nextread.readpick.presentation.community.write

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
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
import com.nextread.readpick.data.model.book.SavedBookDto
import com.nextread.readpick.data.model.community.CommunityBookDto
import com.nextread.readpick.presentation.common.component.ErrorMessage
import com.nextread.readpick.presentation.common.component.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WritePostScreen(
    onClose: () -> Unit,
    onPostCreated: (Long) -> Unit,
    viewModel: WritePostViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showBookSelector by remember { mutableStateOf(false) }

    // 네비게이션 이벤트 처리
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is WritePostViewModel.NavigationEvent.PostCreated -> {
                    onPostCreated(event.postId)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("글쓰기") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "닫기"
                        )
                    }
                },
                actions = {
                    val state = uiState
                    if (state is WritePostUiState.Ready) {
                        TextButton(
                            onClick = viewModel::submitPost,
                            enabled = !state.isSubmitting
                        ) {
                            if (state.isSubmitting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("등록")
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is WritePostUiState.Loading -> {
                LoadingIndicator()
            }
            is WritePostUiState.Ready -> {
                WritePostContent(
                    modifier = Modifier.padding(paddingValues),
                    state = state,
                    onCategorySelect = viewModel::selectCategory,
                    onPostTypeSelect = viewModel::selectPostType,
                    onTitleChange = viewModel::updateTitle,
                    onContentChange = viewModel::updateContent,
                    onBookSelectClick = { showBookSelector = true },
                    onBookRemove = { viewModel.selectBook(null) },
                    onErrorDismiss = viewModel::clearError
                )
            }
            is WritePostUiState.Success -> {
                // 처리 완료 - 네비게이션 이벤트로 처리됨
            }
            is WritePostUiState.Error -> {
                ErrorMessage(
                    message = state.message,
                    onRetry = { /* 재시도 */ }
                )
            }
        }
    }

    // 책 선택 바텀시트
    if (showBookSelector) {
        val state = uiState
        if (state is WritePostUiState.Ready) {
            BookSelectorBottomSheet(
                savedBooks = state.savedBooks,
                onDismiss = { showBookSelector = false },
                onBookSelect = { book ->
                    viewModel.selectBook(book)
                    showBookSelector = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WritePostContent(
    modifier: Modifier = Modifier,
    state: WritePostUiState.Ready,
    onCategorySelect: (Long) -> Unit,
    onPostTypeSelect: (PostType) -> Unit,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onBookSelectClick: () -> Unit,
    onBookRemove: () -> Unit,
    onErrorDismiss: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 에러 메시지
        state.errorMessage?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    IconButton(onClick = onErrorDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "닫기",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 카테고리 선택
        Text(
            text = "카테고리 선택 *",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        var categoryExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = it }
        ) {
            OutlinedTextField(
                value = state.categories.find { it.id == state.selectedCategoryId }?.name ?: "",
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("카테고리를 선택하세요") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                state.categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = {
                            onCategorySelect(category.id)
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 게시물 타입
        Text(
            text = "게시물 타입 *",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PostType.entries.forEach { postType ->
                FilterChip(
                    selected = state.selectedPostType == postType,
                    onClick = { onPostTypeSelect(postType) },
                    label = { Text(postType.displayName) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 제목
        Text(
            text = "제목 *",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.title,
            onValueChange = onTitleChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("제목을 입력하세요") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 내용
        Text(
            text = "내용 *",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.content,
            onValueChange = onContentChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp),
            placeholder = { Text("내용을 입력하세요") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 책 선택
        if (state.selectedBook == null) {
            OutlinedButton(
                onClick = onBookSelectClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("책 선택하기")
            }
        } else {
            // 선택된 책 표시
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = state.selectedBook.cover,
                        contentDescription = state.selectedBook.title,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = state.selectedBook.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = state.selectedBook.author,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onBookRemove) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "삭제"
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookSelectorBottomSheet(
    savedBooks: List<SavedBookDto>,
    onDismiss: () -> Unit,
    onBookSelect: (CommunityBookDto) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "내 서재에서 책 선택",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (savedBooks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "내 서재에 저장된 책이 없습니다",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn {
                    items(savedBooks) { book ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // SavedBookDto를 CommunityBookDto로 변환
                                    onBookSelect(
                                        CommunityBookDto(
                                            isbn13 = book.isbn13,
                                            title = book.title,
                                            author = book.author,
                                            cover = book.cover
                                        )
                                    )
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = book.cover,
                                contentDescription = book.title,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = book.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = book.author,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
