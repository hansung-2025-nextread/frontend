package com.nextread.readpick.presentation.collection.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nextread.readpick.R
import com.nextread.readpick.ui.theme.NextReadTheme

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
 * 내 서재 탭 컨텐츠 (즐겨찾기한 모든 책)
 *
 * 사용자가 즐겨찾기한 모든 책을 그리드 형태로 표시합니다.
 * 필터 및 편집 기능을 제공합니다.
 *
 * @param bookCount 즐겨찾기한 책의 총 개수
 * @param onFilterClick 필터 버튼 클릭 시 호출
 * @param onEditClick 편집 버튼 클릭 시 호출
 * @param onDeleteBooks 선택된 책들을 삭제(즐겨찾기 취소)할 때 호출
 * @param modifier Modifier
 */
@Composable
fun MyLibraryContent(
    bookCount: Int,
    onFilterClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteBooks: (List<String>) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // TODO: ViewModel에서 실제 즐겨찾기 책 목록 가져오기
    // 임시 데이터
    val dummyBooks = List(bookCount) {
        FavoriteBookDto(
            isbn13 = "978123456789$it",
            title = "책 제목 ${it + 1}",
            author = "저자 ${it + 1}",
            coverUrl = "https://placehold.co/120x180/7F1D1D/ffffff?text=Book+$it"
        )
    }

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
                    // 일반 모드: 필터 및 편집 버튼
                    OutlinedButton(
                        onClick = onFilterClick,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text("필터", fontSize = 12.sp)
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

        // 즐겨찾기 책 그리드
        if (dummyBooks.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(dummyBooks) { book ->
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
                                // TODO: 책 상세 화면으로 이동
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
            .width(100.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.Start
    ) {
        Box {
            // TODO: Coil로 실제 책 표지 이미지 로드
            // 책 표지 이미지
            AsyncImage(
                model = book.coverUrl,
                contentDescription = book.title,
                modifier = Modifier
                    .width(100.dp)
                    .height(150.dp)
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