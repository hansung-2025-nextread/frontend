package com.nextread.readpick.presentation.collection.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nextread.readpick.R
import com.nextread.readpick.data.model.book.SavedBookDto
import com.nextread.readpick.domain.model.ReadingStatus
import com.nextread.readpick.ui.theme.NextReadTheme
import com.nextread.readpick.util.ImageUtils

/**
 * 즐겨찾기한 책 DTO
 *
 * 사용자가 즐겨찾기한 책의 정보를 담는 데이터 클래스
 */
data class FavoriteBookDto(
    val isbn13: String,
    val title: String,
    val author: String,
    val coverUrl: String
)

/**
 * 뷰 모드
 *
 * GRID: 그리드 뷰 (표지만)
 * LIST: 리스트 뷰 (독서 상태 포함)
 */
enum class ViewMode {
    GRID, LIST
}

/**
 * 내 서재 탭 컨텐츠 (즐겨찾기한 모든 책)
 *
 * 사용자가 즐겨찾기한 모든 책을 그리드/리스트 형태로 표시합니다.
 * 필터 및 편집 기능을 제공합니다.
 *
 * @param bookCount 즐겨찾기한 책의 총 개수
 * @param books 즐겨찾기한 책 목록 (그리드 뷰용)
 * @param booksWithStatus 독서 상태 포함된 책 목록 (리스트 뷰용)
 * @param onFilterClick 필터 버튼 클릭 시 호출
 * @param onEditClick 편집 버튼 클릭 시 호출
 * @param onDeleteBooks 선택된 책들을 삭제(즐겨찾기 취소)할 때 호출
 * @param onBookClick 책 클릭 시 상세 화면으로 이동
 * @param onStatusChange 독서 상태 변경 시 호출 (isbn13, newStatus)
 * @param modifier Modifier
 */
@Composable
fun MyLibraryContent(
    bookCount: Int,
    books: List<FavoriteBookDto> = emptyList(),
    booksWithStatus: List<SavedBookDto> = emptyList(),
    onFilterClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteBooks: (List<String>) -> Unit = {},
    onBookClick: (String) -> Unit = {},
    onStatusChange: (String, ReadingStatus) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {

    // 뷰 모드 상태 (기본: 그리드)
    var viewMode by remember { mutableStateOf(ViewMode.GRID) }

    // 편집 모드 상태
    var isEditMode by remember { mutableStateOf(false) }
    var selectedBooks by remember { mutableStateOf<Set<String>>(emptySet()) }

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
            if (isEditMode) {
                Text(
                    text = "${selectedBooks.size}권 선택",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = "${bookCount}권",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (isEditMode) {
                    // 편집 모드: 취소 및 삭제 버튼
                    OutlinedButton(
                        onClick = {
                            isEditMode = false
                            selectedBooks = emptySet()
                        },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text("취소", fontSize = 12.sp)
                    }
                    Button(
                        onClick = {
                            onDeleteBooks(selectedBooks.toList())
                            isEditMode = false
                            selectedBooks = emptySet()
                        },
                        enabled = selectedBooks.isNotEmpty(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("삭제", fontSize = 12.sp)
                    }
                } else {
                    // 일반 모드: 뷰 모드 토글 + 편집 버튼
                    IconButton(
                        onClick = {
                            viewMode = if (viewMode == ViewMode.GRID) ViewMode.LIST else ViewMode.GRID
                        }
                    ) {
                        Icon(
                            imageVector = if (viewMode == ViewMode.GRID) Icons.Default.MoreVert else Icons.Default.Star,
                            contentDescription = if (viewMode == ViewMode.GRID) "리스트 뷰로 전환" else "그리드 뷰로 전환"
                        )
                    }
                    Button(
                        onClick = {
                            isEditMode = true
                            onEditClick()
                        },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text("편집", fontSize = 12.sp)
                    }
                }
            }
        }

        // 뷰 모드에 따른 컨텐츠 표시
        when (viewMode) {
            ViewMode.GRID -> {
                // 그리드 뷰 (기존)
                if (books.isNotEmpty()) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 120.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(books) { book ->
                            FavoriteBookCoverItem(
                                book = book,
                                isEditMode = isEditMode,
                                isSelected = selectedBooks.contains(book.isbn13),
                                onClick = {
                                    if (isEditMode) {
                                        // 편집 모드: 선택/해제
                                        selectedBooks = if (selectedBooks.contains(book.isbn13)) {
                                            selectedBooks - book.isbn13
                                        } else {
                                            selectedBooks + book.isbn13
                                        }
                                    } else {
                                        // 일반 모드: 책 상세 화면으로 이동
                                        onBookClick(book.isbn13)
                                    }
                                }
                            )
                        }
                    }
                } else {
                    // 즐겨찾기한 책이 없을 경우
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "즐겨찾기한 책이 없습니다.\n홈에서 책을 즐겨찾기해 보세요!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            ViewMode.LIST -> {
                // 리스트 뷰 (독서 상태 포함)
                if (booksWithStatus.isNotEmpty()) {
                    LazyColumn(
                        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(booksWithStatus) { book ->
                            SavedBookCard(
                                book = book,
                                onStatusChange = onStatusChange,
                                onBookClick = onBookClick
                            )
                        }
                    }
                } else {
                    // 즐겨찾기한 책이 없을 경우
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "즐겨찾기한 책이 없습니다.\n홈에서 책을 즐겨찾기해 보세요!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * 즐겨찾기한 책 표지 아이템
 *
 * @param book 즐겨찾기한 책 정보
 * @param isEditMode 편집 모드 여부
 * @param isSelected 선택된 상태 여부 (편집 모드에서만 사용)
 * @param onClick 책 클릭 시 호출되는 콜백
 */
@Composable
fun FavoriteBookCoverItem(
    book: FavoriteBookDto,
    isEditMode: Boolean = false,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.Start
    ) {
        Box {
            // 책 표지 이미지 (고화질 이미지 URL로 변환)
            AsyncImage(
                model = ImageUtils.getHighQualityCoverUrl(book.coverUrl),
                contentDescription = book.title,
                modifier = Modifier
                    .width(120.dp)
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_menu),
                error = painterResource(id = R.drawable.ic_menu)
            )

            // 편집 모드일 때 체크박스 표시
            if (isEditMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onClick() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 책 제목
        Text(
            text = book.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        // 저자 이름
        Text(
            text = book.author,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MyLibraryContentPreview() {
    NextReadTheme {
        MyLibraryContent(
            bookCount = 12,
            onFilterClick = {},
            onEditClick = {}
        )
    }
}