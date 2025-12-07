package com.nextread.readpick.presentation.collection.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nextread.readpick.R
import com.nextread.readpick.data.model.book.SavedBookDto
import com.nextread.readpick.domain.model.ReadingStatus
import com.nextread.readpick.util.ImageUtils

/**
 * 내 서재 책 카드
 *
 * 책 표지, 제목, 저자, 독서 상태 드롭다운, 날짜 정보를 표시합니다.
 *
 * @param book 저장된 책 정보
 * @param onStatusChange 독서 상태 변경 시 호출 (isbn13, newStatus)
 * @param onBookClick 책 클릭 시 호출
 * @param modifier Modifier
 */
@Composable
fun SavedBookCard(
    book: SavedBookDto,
    onStatusChange: (String, ReadingStatus) -> Unit,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 책 표지
            AsyncImage(
                model = ImageUtils.getHighQualityCoverUrl(book.cover),
                contentDescription = book.title,
                modifier = Modifier
                    .width(80.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onBookClick(book.isbn13) },
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_menu),
                error = painterResource(id = R.drawable.ic_menu)
            )

            // 책 정보 + 상태
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 제목
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable { onBookClick(book.isbn13) }
                )

                // 저자
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // 독서 상태 드롭다운
                ReadingStatusDropdown(
                    currentStatus = ReadingStatus.fromString(book.readingStatus),
                    onStatusChange = { newStatus ->
                        onStatusChange(book.isbn13, newStatus)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // 날짜 표시
                if (book.startedAt != null || book.completedAt != null) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        book.startedAt?.let { startedAt ->
                            Text(
                                text = "시작: $startedAt",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        book.completedAt?.let { completedAt ->
                            Text(
                                text = "완독: $completedAt",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
