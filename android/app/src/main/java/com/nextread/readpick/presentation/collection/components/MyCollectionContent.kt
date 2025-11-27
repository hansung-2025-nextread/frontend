package com.nextread.readpick.presentation.collection.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.unit.sp
import com.nextread.readpick.R
import com.nextread.readpick.ui.theme.NextReadTheme

// 임시 컬렉션 DTO
data class UserCollection(
    val id: Long,
    val name: String,
    val bookCount: Int,
    val latestCoverUrl: String? = null
)

@Composable
fun MyCollectionContent(
    hasCustomCollections: Boolean,
    onMakeCollectionClick: () -> Unit,
    onEditClick: () -> Unit,
    onCollectionClick: (collectionId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!hasCustomCollections) {
        // 스크린샷 1: 컬렉션이 없을 때
        EmptyCollectionState(onMakeCollectionClick)
    } else {
        // 스크린샷 2: 컬렉션이 있을 때
        CollectionListState(
            onEditClick = onEditClick,
            onCollectionClick = onCollectionClick
        )
    }
}

@Composable
fun EmptyCollectionState(onMakeCollectionClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "내 책장을 만들어 보세요.",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "원하는 대로 책을 담고 분류할 수 있어요",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onMakeCollectionClick,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("내 책장 만들기", color = Color.White)
            }
        }
    }
}

@Composable
fun CollectionListState(
    onEditClick: () -> Unit,
    onCollectionClick: (collectionId: Long) -> Unit
) {
    // 임시 데이터 (스크린샷 기반)
    val dummyCollections = listOf(
        UserCollection(
            id = 1,
            name = "재미있게 읽은 소설",
            bookCount = 5,
            latestCoverUrl = "https://placehold.co/120x180/7F1D1D/ffffff?text=Cover1"
        ),
        UserCollection(
            id = 2,
            name = "다음 프로젝트를 위한 자료 모음",
            bookCount = 10,
            latestCoverUrl = "https://placehold.co/120x180/1D7F34/ffffff?text=Cover2"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // 편집 버튼
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = onEditClick, contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)) {
                Text("편집", fontSize = 12.sp)
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(dummyCollections) { collection ->
                CollectionListItem(
                    collection = collection,
                    onClick = { onCollectionClick(collection.id) }
                )
            }
            // 하단 네비게이션 공간 확보
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun CollectionListItem(collection: UserCollection, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 책 표지 이미지 플레이스홀더
        Box(
            modifier = Modifier
                .size(60.dp, 90.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Gray.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = collection.name.first().toString(),
                color = Color.White,
                fontSize = 24.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = collection.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${collection.bookCount}권의 책",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "컬렉션 상세 보기"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MyCollectionContentEmptyPreview() {
    NextReadTheme {
        MyCollectionContent(
            hasCustomCollections = false,
            onMakeCollectionClick = {},
            onEditClick = {},
            onCollectionClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MyCollectionContentListPreview() {
    NextReadTheme {
        MyCollectionContent(
            hasCustomCollections = true,
            onMakeCollectionClick = {},
            onEditClick = {},
            onCollectionClick = {}
        )
    }
}