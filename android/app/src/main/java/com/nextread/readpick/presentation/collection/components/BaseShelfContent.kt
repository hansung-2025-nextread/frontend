package com.nextread.readpick.presentation.collection.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nextread.readpick.R
import com.nextread.readpick.data.model.user.UserInfoDto
import com.nextread.readpick.ui.theme.NextReadTheme
import androidx.compose.ui.unit.sp // 🚨 이 줄을 추가합니다.

// 임시 도서 DTO (MyLibrary에서 사용될 수 있음)
data class ShelfBookDto(
    val isbn13: String,
    val title: String,
    val coverUrl: String,
)

@Composable
fun BaseShelfContent(
    bookCount: Int,
    onFilterClick: () -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 임시 데이터
    val dummyBooks = List(bookCount) {
        ShelfBookDto(
            isbn13 = "978123456789$it",
            title = "인간 실격",
            coverUrl = "https://placehold.co/120x180/7F1D1D/ffffff?text=Book+Cover",
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${bookCount}권",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // 스크린샷에는 필터 버튼이 없으나, '편집' 옆에 자주 위치함
                OutlinedButton(onClick = onFilterClick, contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)) {
                    Text("필터", fontSize = 12.sp)
                }
                Button(onClick = onEditClick, contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)) {
                    Text("편집", fontSize = 12.sp)
                }
            }
        }

        // 도서 목록 그리드
        if (dummyBooks.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(dummyBooks) { book ->
                    BookCoverItem(book = book)
                }
            }
        } else {
            // 책이 없을 경우 (기본 책장은 항상 비어있지 않다고 가정)
            Text("기본 책장에 저장된 책이 없습니다.")
        }
    }
}

@Composable
fun BookCoverItem(book: ShelfBookDto) {
    Column(
        modifier = Modifier.width(100.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // 책 표지 이미지 (Coil 등으로 대체될 부분)
        Image(
            painter = painterResource(id = R.drawable.ic_menu), // 임시 Placeholder
            contentDescription = book.title,
            modifier = Modifier
                .width(100.dp)
                .height(150.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = book.title,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BaseShelfContentPreview() {
    NextReadTheme {
        BaseShelfContent(
            bookCount = 3,
            onFilterClick = {},
            onEditClick = {}
        )
    }
}