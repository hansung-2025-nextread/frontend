package com.nextread.readpick.presentation.collection

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nextread.readpick.R
import com.nextread.readpick.presentation.collection.components.FavoriteBookDto
import com.nextread.readpick.ui.theme.NextReadTheme

/**
 * 선택 가능한 책 데이터 클래스
 *
 * 컬렉션에 추가할 책을 선택하기 위한 래퍼 클래스
 *
 * @param book 즐겨찾기한 책 정보
 * @param isSelected 현재 선택된 상태 여부
 */
data class SelectableBook(
    val book: FavoriteBookDto,
    val isSelected: Boolean = false
)

/**
 * 컬렉션 생성 2단계: 책 선택 화면
 *
 * 내 서재(즐겨찾기)의 책들 중에서 새로운 컬렉션에 추가할 책을 선택합니다.
 *
 * @param collectionName 1단계에서 입력한 컬렉션 이름
 * @param onDismiss 닫기 버튼 클릭 시 호출
 * @param onComplete 완료 버튼 클릭 시 호출 (컬렉션 이름, 선택된 책 ISBN 목록 전달)
 * @param modifier Modifier
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionSelectBookScreen(
    collectionName: String,
    onDismiss: () -> Unit,
    onComplete: (name: String, selectedIsbns: List<String>) -> Unit,
    modifier: Modifier = Modifier
    // TODO: ViewModel 연동 필요
    // viewModel: CollectionViewModel = hiltViewModel()
) {
    // TODO: ViewModel에서 내 서재(즐겨찾기)의 전체 책 목록 가져오기
    // 현재는 더미 데이터로 대체
    val initialBooks = remember {
        List(10) {
            FavoriteBookDto(
                isbn13 = "978-00611200${it}",
                title = "책 제목 ${it + 1}",
                author = "저자 ${it + 1}",
                coverUrl = ""
            )
        }
    }

    // 선택 상태를 관리하는 mutableStateList
    var selectableBooks by remember {
        mutableStateOf(initialBooks.map { SelectableBook(it) })
    }

    val selectedCount = selectableBooks.count { it.isSelected }
    val selectedIsbns = selectableBooks.filter { it.isSelected }.map { it.book.isbn13 }

    // 개별 책 선택/해제 핸들러
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
                title = { Text("책장 추가", fontWeight = FontWeight.Bold) },
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
            // 완료 버튼
            Button(
                onClick = { onComplete(collectionName, selectedIsbns) },
                enabled = selectedCount > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("${selectedCount}권 선택 완료")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            // 02. 책 추가 안내
            Text(
                text = "02. \"$collectionName\" 책장에 추가할 도서를 선택하세요.",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

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

/**
 * 선택 가능한 책 아이템
 *
 * 책 표지, 제목, 저자와 함께 체크박스를 표시하여 선택할 수 있도록 합니다.
 *
 * @param sBook 선택 가능한 책 정보
 * @param onSelect 책 선택/해제 시 호출되는 콜백 (ISBN, 선택 상태)
 */
@Composable
fun SelectableBookItem(
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
            // TODO: AsyncImage로 실제 책 표지 로드
            Image(
                painter = painterResource(id = R.drawable.ic_menu),
                contentDescription = book.title,
                modifier = Modifier
                    .size(40.dp, 60.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
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


@Preview(showBackground = true)
@Composable
fun CollectionSelectBookScreenPreview() {
    NextReadTheme {
        CollectionSelectBookScreen(
            collectionName = "주말 독서 모음",
            onDismiss = {},
            onComplete = { _, _ -> }
        )
    }
}