package com.nextread.readpick.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.nextread.readpick.R
import com.nextread.readpick.data.model.search.SearchBookDto
import java.text.DecimalFormat

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onBookClick: (String) -> Unit // 책 상세로 이동
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            SearchTopBar(
                query = query,
                onQueryChange = viewModel::onQueryChange,
                onSearch = {
                    viewModel.searchBooks()
                    keyboardController?.hide() // 검색 시 키보드 내림
                },
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (val state = uiState) {
                is SearchUiState.Idle -> {
                    // 검색 전 초기 화면
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "검색어를 입력하고 책을 찾아보세요", color = Color.Gray)
                    }
                }
                is SearchUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is SearchUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is SearchUiState.Success -> {
                    if (state.books.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "검색 결과가 없습니다.")
                        }
                    } else {
                        SearchResultList(
                            books = state.books,
                            onBookClick = onBookClick
                        )
                    }
                }
            }
        }
    }
}

/**
 * 상단 검색 바
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "뒤로가기")
        }

        // 검색 입력창
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("책 제목, 저자 검색") },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp)
                .clip(RoundedCornerShape(8.dp)),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true,
            // 키보드 설정 (검색 버튼 표시)
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() }),
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "지우기")
                    }
                }
            }
        )

        IconButton(onClick = onSearch) {
            Icon(imageVector = Icons.Default.Search, contentDescription = "검색")
        }
    }
}

/**
 * 검색 결과 리스트
 */
@Composable
fun SearchResultList(
    books: List<SearchBookDto>,
    onBookClick: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(books) { book ->
            SearchBookItem(book = book, onBookClick = onBookClick)
        }
    }
}

/**
 * 개별 책 아이템 (가로형)
 */
@Composable
fun SearchBookItem(
    book: SearchBookDto,
    onBookClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onBookClick(book.isbn13) }
            .height(140.dp)
    ) {
        // 1. 책 표지
        AsyncImage(
            model = book.cover,
            contentDescription = book.title,
            modifier = Modifier
                .width(90.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_menu), // TODO: 적절한 Placeholder 교체
            error = painterResource(id = R.drawable.ic_menu)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // 2. 책 정보
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            // 제목
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 저자 | 출판사
            val authorInfo = listOfNotNull(book.author, book.publisher).joinToString(" | ")
            Text(
                text = authorInfo,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 가격 정보 (15,000원 형식)
            book.priceStandard?.let { price ->
                val decimalFormat = DecimalFormat("#,###")
                Text(
                    text = "${decimalFormat.format(price)}원",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 설명 (있는 경우에만)
            book.description?.let { desc ->
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}