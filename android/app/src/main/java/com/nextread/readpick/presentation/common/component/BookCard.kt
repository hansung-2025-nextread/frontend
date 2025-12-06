package com.nextread.readpick.presentation.common.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nextread.readpick.util.ImageUtils

/**
 * 공통 책 카드 컴포넌트
 *
 * 모든 화면에서 재사용 가능한 책 표시 카드
 * - 홈 화면의 베스트셀러 목록
 * - 검색 결과
 * - 내 서재
 * - 컬렉션 등
 *
 * @param isbn13 책 ISBN (고유 ID)
 * @param title 책 제목
 * @param author 저자
 * @param coverUrl 표지 이미지 URL
 * @param onClick 클릭 이벤트 (보통 도서 상세 화면으로 이동)
 * @param modifier Modifier
 *
 * 사용 예시:
 * ```kotlin
 * @Composable
 * fun HomeScreen() {
 *     LazyColumn {
 *         items(books) { book ->
 *             BookCard(
 *                 isbn13 = book.isbn13,
 *                 title = book.title,
 *                 author = book.author,
 *                 coverUrl = book.cover,
 *                 onClick = { navController.navigate("book/${book.isbn13}") }
 *             )
 *         }
 *     }
 * }
 * ```
 */
@Composable
fun BookCard(
    isbn13: String,
    title: String,
    author: String,
    coverUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(120.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // 책 표지 (고화질 이미지 URL로 변환)
            AsyncImage(
                model = ImageUtils.getHighQualityCoverUrl(coverUrl),
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            // 책 정보
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                // 제목 (최대 2줄)
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 저자 (최대 1줄)
                Text(
                    text = author,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
