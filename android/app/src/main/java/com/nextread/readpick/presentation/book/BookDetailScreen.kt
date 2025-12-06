package com.nextread.readpick.presentation.book

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.nextread.readpick.data.model.book.BookDetailDto
import com.nextread.readpick.data.model.review.ReviewDto
import com.nextread.readpick.util.ImageUtils

/**
 * 책 상세 화면
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    viewModel: BookDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val showReviewDialog by viewModel.showReviewDialog.collectAsStateWithLifecycle()
    val showReportDialog by viewModel.showReportDialog.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Snackbar 메시지 수신
    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    when (val state = uiState) {
                        is BookDetailUiState.Success -> {
                            Text(
                                text = state.book.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        else -> Text("책 상세")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "뒤로가기")
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is BookDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is BookDetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Button(onClick = viewModel::refresh) {
                            Text("다시 시도")
                        }
                    }
                }
            }

            is BookDetailUiState.Success -> {
                BookDetailContent(
                    modifier = Modifier.padding(padding),
                    book = state.book,
                    isSaved = state.isSaved,
                    isSaveLoading = state.isSaveLoading,
                    reviews = state.reviews,
                    myReview = state.myReview,
                    hasMoreReviews = !state.reviewsPage.last,
                    isReviewsLoadingMore = state.isReviewsLoadingMore,
                    onSaveClick = viewModel::saveBook,
                    onWriteReviewClick = viewModel::openReviewDialog,
                    onEditReviewClick = viewModel::openReviewDialog,
                    onDeleteReviewClick = viewModel::deleteReview,
                    onReportClick = viewModel::openReportDialog,
                    onLoadMoreReviews = viewModel::loadMoreReviews,
                    onLinkClick = { url ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    }
                )
            }
        }
    }

    // 리뷰 작성/수정 다이얼로그
    if (showReviewDialog) {
        val currentState = uiState as? BookDetailUiState.Success
        ReviewDialog(
            existingContent = currentState?.myReview?.content,
            onDismiss = viewModel::closeReviewDialog,
            onSubmit = viewModel::submitReview
        )
    }

    // 리뷰 신고 다이얼로그
    showReportDialog?.let { review ->
        ReportDialog(
            review = review,
            onDismiss = viewModel::closeReportDialog,
            onSubmit = { reason -> viewModel.reportReview(review, reason) }
        )
    }
}

@Composable
fun BookDetailContent(
    modifier: Modifier = Modifier,
    book: BookDetailDto,
    isSaved: Boolean,
    isSaveLoading: Boolean,
    reviews: List<ReviewDto>,
    myReview: ReviewDto?,
    hasMoreReviews: Boolean,
    isReviewsLoadingMore: Boolean,
    onSaveClick: () -> Unit,
    onWriteReviewClick: () -> Unit,
    onEditReviewClick: () -> Unit,
    onDeleteReviewClick: () -> Unit,
    onReportClick: (ReviewDto) -> Unit,
    onLoadMoreReviews: () -> Unit,
    onLinkClick: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 책 표지 (고화질 이미지 URL로 변환)
        item {
            AsyncImage(
                model = ImageUtils.getHighQualityCoverUrl(book.cover),
                contentDescription = book.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Fit
            )
        }

        // 책 정보
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${book.publisher} · ${book.pubDate}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 평점 표시
        item {
            RatingDisplay(
                ratingScore = book.ratingScore,
                ratingCount = book.ratingCount
            )
        }

        // 가격 정보
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text(
                        text = "판매가",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${book.priceSales}원",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text(
                        text = "정가",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${book.priceStandard}원",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 저장 버튼
        item {
            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaved && !isSaveLoading
            ) {
                if (isSaveLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (isSaved) "저장됨" else "내 서재에 저장")
                }
            }
        }

        // 책 설명
        item {
            var expanded by remember { mutableStateOf(false) }
            Column {
                Text(
                    text = "책 소개",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = book.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = if (expanded) Int.MAX_VALUE else 5,
                    overflow = TextOverflow.Ellipsis
                )
                if (book.description.length > 200) {
                    TextButton(onClick = { expanded = !expanded }) {
                        Text(if (expanded) "접기" else "더보기")
                    }
                }
            }
        }

        // 알라딘 링크
        item {
            OutlinedButton(
                onClick = { onLinkClick(book.link) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("알라딘에서 보기")
            }
        }

        // 구분선
        item {
            HorizontalDivider()
        }

        // 리뷰 섹션 헤더
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "리뷰 ${reviews.size}개",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 내 리뷰
        myReview?.let { review ->
            item {
                MyReviewCard(
                    review = review,
                    onEditClick = onEditReviewClick,
                    onDeleteClick = onDeleteReviewClick
                )
            }
        }

        // 리뷰 작성 버튼
        if (isSaved && myReview == null) {
            item {
                OutlinedButton(
                    onClick = onWriteReviewClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("리뷰 작성하기")
                }
            }
        }

        // 다른 사용자 리뷰 목록
        val otherReviews = reviews.filter { it != myReview }
        items(otherReviews) { review ->
            ReviewCard(
                review = review,
                onReportClick = { onReportClick(review) }
            )
        }

        // 더보기 버튼
        if (hasMoreReviews) {
            item {
                if (isReviewsLoadingMore) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    OutlinedButton(
                        onClick = onLoadMoreReviews,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("더 보기")
                    }
                }
            }
        }

        // 빈 리뷰 상태
        if (reviews.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "첫 번째 리뷰를 작성해보세요",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun RatingDisplay(
    ratingScore: Double,
    ratingCount: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 별 아이콘 표시 (5개 중 채워진 개수)
        val fullStars = ratingScore.toInt()
        val hasHalfStar = ratingScore - fullStars >= 0.5

        repeat(fullStars) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
        if (hasHalfStar && fullStars < 5) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
        repeat(5 - fullStars - if (hasHalfStar) 1 else 0) {
            Icon(
                imageVector = Icons.Outlined.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$ratingScore (${ratingCount}명)",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ReviewCard(
    review: ReviewDto,
    onReportClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = review.userPicture,
                        contentDescription = review.userName,
                        modifier = Modifier.size(32.dp)
                    )
                    Column {
                        Text(
                            text = review.userName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = review.createdAt,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = onReportClick) {
                    Icon(
                        Icons.Filled.MoreVert,
                        contentDescription = "신고",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            Text(
                text = review.content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun MyReviewCard(
    review: ReviewDto,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "내 리뷰",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, "수정")
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, "삭제")
                    }
                }
            }
            Text(
                text = review.content,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = review.createdAt,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ReviewDialog(
    existingContent: String?,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var content by remember { mutableStateOf(existingContent ?: "") }
    val maxLength = 500

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existingContent != null) "리뷰 수정" else "리뷰 작성") },
        text = {
            Column {
                OutlinedTextField(
                    value = content,
                    onValueChange = { if (it.length <= maxLength) content = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("리뷰 내용을 입력하세요 (최소 10자)") },
                    minLines = 5,
                    maxLines = 10
                )
                Text(
                    text = "${content.length} / $maxLength",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSubmit(content) },
                enabled = content.length >= 10
            ) {
                Text("저장")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

@Composable
fun ReportDialog(
    review: ReviewDto,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var selectedReason by remember { mutableStateOf<ReportReason?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("리뷰 신고") },
        text = {
            Column {
                Text(
                    text = "신고 사유를 선택해주세요",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                ReportReason.values().forEach { reason ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedReason == reason,
                            onClick = { selectedReason = reason }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = reason.displayText,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedReason?.let { onSubmit(it.value) }
                },
                enabled = selectedReason != null
            ) {
                Text("신고")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

/**
 * 신고 사유 enum
 */
enum class ReportReason(val displayText: String, val value: String) {
    SPAM("스팸", "SPAM"),
    PROFANITY("욕설 및 비방", "PROFANITY"),
    INAPPROPRIATE("부적절한 내용", "INAPPROPRIATE"),
    OFF_TOPIC("주제와 무관", "OFF_TOPIC"),
    OTHER("기타", "OTHER")
}
