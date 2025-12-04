package com.nextread.readpick.presentation.collection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.nextread.readpick.R
import com.nextread.readpick.presentation.collection.components.FavoriteBookDto

/**
 * 컬렉션에 책 추가 화면
 *
 * 기존 컬렉션에 저장된 책들을 추가할 수 있는 화면입니다.
 *
 * @param collectionId 책을 추가할 컬렉션 ID
 * @param collectionName 컬렉션 이름
 * @param parentEntry 부모 화면의 NavBackStackEntry (ViewModel 공유용)
 * @param onDismiss 닫기 또는 완료 후 호출
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionAddBookScreen(
    collectionId: Long,
    collectionName: String,
    parentEntry: NavBackStackEntry?,
    onDismiss: () -> Unit
) {
    // 부모 화면(CollectionScreen)의 ViewModel을 공유
    val viewModel: CollectionViewModel = if (parentEntry != null) {
        hiltViewModel(parentEntry)
    } else {
        hiltViewModel()
    }

    // ViewModel에서 저장된 책 목록 가져오기
    val uiState by viewModel.uiState.collectAsState()
    val savedBooks = uiState.savedBooks

    // 선택 상태 관리
    var selectableBooks by remember { mutableStateOf<List<SelectableBook>>(emptyList()) }

    // 저장된 책 목록이 변경될 때마다 업데이트
    LaunchedEffect(savedBooks) {
        selectableBooks = savedBooks.map { SelectableBook(it) }
    }

    val selectedCount = selectableBooks.count { it.isSelected }
    val selectedIsbns = selectableBooks.filter { it.isSelected }.map { it.book.isbn13 }

    // 책 선택/해제 핸들러
    val onBookSelect: (String, Boolean) -> Unit = { isbn13, isSelected ->
        selectableBooks = selectableBooks.map { sBook ->
            if (sBook.book.isbn13 == isbn13) {
                sBook.copy(isSelected = isSelected)
            } else {
                sBook
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("책 추가", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "닫기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            // 추가 버튼
            Button(
                onClick = {
                    // API 호출하여 선택한 책들을 컬렉션에 추가
                    viewModel.addBooksToCollection(collectionId, selectedIsbns)
                    onDismiss()
                },
                enabled = selectedCount > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (selectedCount > 0) "${selectedCount}권 추가" else "추가"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            // 안내 문구
            Text(
                text = "\"$collectionName\" 책장에 추가할 도서를 선택하세요.",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // 저장된 책이 없을 때
            if (savedBooks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "저장된 책이 없습니다.\n홈에서 책을 저장해보세요!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // 도서 목록
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(selectableBooks) { sBook ->
                        SelectableBookItem(
                            sBook = sBook,
                            onSelect = onBookSelect
                        )
                    }
                    // 하단 버튼과의 간격 확보
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

/**
 * 선택 가능한 책 아이템
 */
@Composable
private fun SelectableBookItem(
    sBook: SelectableBook,
    onSelect: (String, Boolean) -> Unit
) {
    val book = sBook.book
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onSelect(book.isbn13, !sBook.isSelected) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 책 표지
            coil.compose.AsyncImage(
                model = book.coverUrl.ifEmpty { null },
                contentDescription = book.title,
                modifier = Modifier
                    .size(40.dp, 60.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_menu),
                error = painterResource(id = R.drawable.ic_menu)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // 체크박스
        Checkbox(
            checked = sBook.isSelected,
            onCheckedChange = { onSelect(book.isbn13, it) }
        )
    }
}