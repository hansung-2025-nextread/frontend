package com.nextread.readpick.presentation.book

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextread.readpick.data.local.TokenManager
import com.nextread.readpick.data.model.review.ReviewDto
import com.nextread.readpick.data.model.search.PageInfo
import com.nextread.readpick.domain.repository.BookRepository
import com.nextread.readpick.domain.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 책 상세 화면 ViewModel
 */
@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val reviewRepository: ReviewRepository,
    private val tokenManager: TokenManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val isbn13: String = savedStateHandle.get<String>("isbn13") ?: ""

    // 현재 로그인한 사용자 이름 (내 리뷰 식별용)
    private val currentUserName: String? = tokenManager.getName()

    private val _uiState = MutableStateFlow<BookDetailUiState>(BookDetailUiState.Loading)
    val uiState: StateFlow<BookDetailUiState> = _uiState.asStateFlow()

    // 다이얼로그 상태
    private val _showReviewDialog = MutableStateFlow(false)
    val showReviewDialog: StateFlow<Boolean> = _showReviewDialog.asStateFlow()

    private val _showReportDialog = MutableStateFlow<ReviewDto?>(null)
    val showReportDialog: StateFlow<ReviewDto?> = _showReportDialog.asStateFlow()

    // Snackbar 메시지
    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    // 내부 상태 관리
    private var currentReviewPage = 0
    private var hasMoreReviews = true
    private val allReviews = mutableListOf<ReviewDto>()

    init {
        loadBookDetail()
    }

    /**
     * 책 상세 정보 및 초기 리뷰 로드
     */
    fun loadBookDetail() {
        viewModelScope.launch {
            _uiState.value = BookDetailUiState.Loading

            // 1. 책 상세 정보 로드
            bookRepository.getBookDetail(isbn13)
                .onSuccess { book ->
                    Log.d(TAG, "책 상세 조회 성공: ${book.title}")

                    // 2. 책이 저장되어 있는지 확인
                    val isSaved = checkIfBookIsSaved()
                    Log.d(TAG, "책 저장 여부: $isSaved")

                    // 3. 초기 리뷰 로드
                    loadReviews(isInitialLoad = true, book = book, isSaved = isSaved)
                }
                .onFailure { exception ->
                    Log.e(TAG, "책 상세 조회 실패", exception)
                    _uiState.value = BookDetailUiState.Error(
                        exception.message ?: "책 정보를 불러올 수 없습니다"
                    )
                }
        }
    }

    /**
     * 책이 내 서재에 저장되어 있는지 확인
     */
    private suspend fun checkIfBookIsSaved(): Boolean {
        return bookRepository.getSavedBooks()
            .map { savedBooks ->
                savedBooks.any { it.isbn13 == isbn13 }
            }
            .getOrDefault(false)
    }

    /**
     * 리뷰 로드 (페이지네이션)
     */
    private fun loadReviews(
        isInitialLoad: Boolean,
        book: com.nextread.readpick.data.model.book.BookDetailDto? = null,
        isSaved: Boolean? = null
    ) {
        viewModelScope.launch {
            if (isInitialLoad) {
                currentReviewPage = 0
                allReviews.clear()
            } else {
                // 더보기 로딩 표시
                val currentState = _uiState.value
                if (currentState is BookDetailUiState.Success) {
                    _uiState.value = currentState.copy(isReviewsLoadingMore = true)
                }
            }

            reviewRepository.getReviews(isbn13, currentReviewPage)
                .onSuccess { pageResponse ->
                    Log.d(TAG, "리뷰 조회 성공: ${pageResponse.content.size}개 (page: $currentReviewPage)")

                    allReviews.addAll(pageResponse.content)
                    hasMoreReviews = !pageResponse.last
                    currentReviewPage++

                    // 내 리뷰 찾기 (userName 기준)
                    val myReview = allReviews.find { it.userName == currentUserName }

                    // UI 상태 업데이트
                    val currentState = _uiState.value
                    val bookData = book ?: (currentState as? BookDetailUiState.Success)?.book

                    if (bookData != null) {
                        _uiState.value = BookDetailUiState.Success(
                            book = bookData,
                            isSaved = isSaved ?: (currentState as? BookDetailUiState.Success)?.isSaved ?: false,
                            reviews = allReviews.toList(),
                            myReview = myReview,
                            reviewsPage = PageInfo(
                                number = pageResponse.number,
                                size = pageResponse.size,
                                totalElements = pageResponse.totalElements,
                                totalPages = pageResponse.totalPages,
                                last = pageResponse.last,
                                first = pageResponse.first
                            ),
                            isReviewsLoadingMore = false
                        )
                    }
                }
                .onFailure { exception ->
                    Log.e(TAG, "리뷰 조회 실패", exception)

                    if (isInitialLoad && book != null) {
                        // 초기 로드 실패 - 빈 리뷰로 책 정보 표시
                        _uiState.value = BookDetailUiState.Success(
                            book = book,
                            isSaved = isSaved ?: false,
                            reviews = emptyList(),
                            myReview = null,
                            reviewsPage = PageInfo()
                        )
                        viewModelScope.launch {
                            _snackbarMessage.emit("리뷰를 불러올 수 없습니다")
                        }
                    } else {
                        // 더보기 실패 - 로딩 표시만 중지
                        val currentState = _uiState.value
                        if (currentState is BookDetailUiState.Success) {
                            _uiState.value = currentState.copy(isReviewsLoadingMore = false)
                        }
                    }
                }
        }
    }

    /**
     * 더 많은 리뷰 로드 (페이지네이션)
     */
    fun loadMoreReviews() {
        val currentState = _uiState.value
        if (currentState is BookDetailUiState.Success &&
            !currentState.isReviewsLoadingMore &&
            hasMoreReviews
        ) {
            Log.d(TAG, "더 많은 리뷰 로드: page=$currentReviewPage")
            loadReviews(isInitialLoad = false)
        }
    }

    /**
     * 책을 내 서재에 저장
     */
    fun saveBook() {
        val currentState = _uiState.value
        if (currentState !is BookDetailUiState.Success || currentState.isSaveLoading) return

        viewModelScope.launch {
            _uiState.value = currentState.copy(isSaveLoading = true)
            Log.d(TAG, "책 저장 시작: isbn13=$isbn13")

            bookRepository.saveBook(isbn13)
                .onSuccess {
                    Log.d(TAG, "책 저장 성공")
                    val state = _uiState.value
                    if (state is BookDetailUiState.Success) {
                        _uiState.value = state.copy(
                            isSaved = true,
                            isSaveLoading = false
                        )
                        _snackbarMessage.emit("내 서재에 저장되었습니다")
                    }
                }
                .onFailure { exception ->
                    Log.e(TAG, "책 저장 실패", exception)
                    val state = _uiState.value
                    if (state is BookDetailUiState.Success) {
                        _uiState.value = state.copy(isSaveLoading = false)
                    }
                    _snackbarMessage.emit(exception.message ?: "저장에 실패했습니다")
                }
        }
    }

    /**
     * 리뷰 다이얼로그 열기
     */
    fun openReviewDialog() {
        val currentState = _uiState.value
        if (currentState is BookDetailUiState.Success) {
            if (currentState.isSaved) {
                _showReviewDialog.value = true
            } else {
                viewModelScope.launch {
                    _snackbarMessage.emit("책을 저장해야 리뷰를 작성할 수 있습니다")
                }
            }
        }
    }

    /**
     * 리뷰 다이얼로그 닫기
     */
    fun closeReviewDialog() {
        _showReviewDialog.value = false
    }

    /**
     * 리뷰 제출 (작성 or 수정)
     */
    fun submitReview(content: String) {
        val currentState = _uiState.value
        if (currentState !is BookDetailUiState.Success) return

        viewModelScope.launch {
            _uiState.value = currentState.copy(isReviewLoading = true)
            closeReviewDialog()

            val isUpdate = currentState.myReview != null
            Log.d(TAG, "리뷰 ${if (isUpdate) "수정" else "작성"} 시작")

            val result = if (isUpdate) {
                reviewRepository.updateReview(isbn13, content)
            } else {
                reviewRepository.createReview(isbn13, content)
            }

            result
                .onSuccess { newReview ->
                    Log.d(TAG, "리뷰 ${if (isUpdate) "수정" else "작성"} 성공")
                    // 리뷰 목록 새로고침
                    loadReviews(isInitialLoad = true, book = currentState.book, isSaved = currentState.isSaved)
                    _snackbarMessage.emit(
                        if (isUpdate) "리뷰가 수정되었습니다" else "리뷰가 작성되었습니다"
                    )
                }
                .onFailure { exception ->
                    Log.e(TAG, "리뷰 ${if (isUpdate) "수정" else "작성"} 실패", exception)
                    val state = _uiState.value
                    if (state is BookDetailUiState.Success) {
                        _uiState.value = state.copy(isReviewLoading = false)
                    }
                    _snackbarMessage.emit(
                        exception.message ?: "리뷰 ${if (isUpdate) "수정" else "작성"}에 실패했습니다"
                    )
                }
        }
    }

    /**
     * 내 리뷰 삭제
     */
    fun deleteReview() {
        val currentState = _uiState.value
        if (currentState !is BookDetailUiState.Success || currentState.myReview == null) return

        viewModelScope.launch {
            _uiState.value = currentState.copy(isReviewLoading = true)
            Log.d(TAG, "리뷰 삭제 시작")

            reviewRepository.deleteReview(isbn13)
                .onSuccess {
                    Log.d(TAG, "리뷰 삭제 성공")
                    // 리뷰 목록 새로고침
                    loadReviews(isInitialLoad = true, book = currentState.book, isSaved = currentState.isSaved)
                    _snackbarMessage.emit("리뷰가 삭제되었습니다")
                }
                .onFailure { exception ->
                    Log.e(TAG, "리뷰 삭제 실패", exception)
                    val state = _uiState.value
                    if (state is BookDetailUiState.Success) {
                        _uiState.value = state.copy(isReviewLoading = false)
                    }
                    _snackbarMessage.emit(exception.message ?: "리뷰 삭제에 실패했습니다")
                }
        }
    }

    /**
     * 리뷰 신고 다이얼로그 열기
     */
    fun openReportDialog(review: ReviewDto) {
        if (review.userName == currentUserName) {
            viewModelScope.launch {
                _snackbarMessage.emit("자신의 리뷰는 신고할 수 없습니다")
            }
        } else {
            _showReportDialog.value = review
        }
    }

    /**
     * 리뷰 신고 다이얼로그 닫기
     */
    fun closeReportDialog() {
        _showReportDialog.value = null
    }

    /**
     * 리뷰 신고 제출
     */
    fun reportReview(review: ReviewDto, reason: String) {
        viewModelScope.launch {
            closeReportDialog()
            Log.d(TAG, "리뷰 신고 시작: reviewId=${review.id}, reason=$reason")

            reviewRepository.reportReview(review.id, reason)
                .onSuccess {
                    Log.d(TAG, "리뷰 신고 성공")
                    _snackbarMessage.emit("신고가 접수되었습니다")
                }
                .onFailure { exception ->
                    Log.e(TAG, "리뷰 신고 실패", exception)
                    _snackbarMessage.emit(exception.message ?: "신고에 실패했습니다")
                }
        }
    }

    /**
     * 전체 새로고침
     */
    fun refresh() {
        Log.d(TAG, "새로고침")
        loadBookDetail()
    }

    companion object {
        private const val TAG = "BookDetailViewModel"
    }
}
