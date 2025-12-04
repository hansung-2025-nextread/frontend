package com.nextread.readpick.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextread.readpick.data.model.search.SearchBookDto
import com.nextread.readpick.data.model.search.SearchLogDto
import com.nextread.readpick.data.model.search.SortType
import com.nextread.readpick.domain.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    // 검색어 상태
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    // 화면 상태 (로딩, 결과 등)
    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    // 정렬 타입
    private val _sortType = MutableStateFlow(SortType.ACCURACY)
    val sortType: StateFlow<SortType> = _sortType.asStateFlow()

    // 선택된 카테고리 필터
    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId.asStateFlow()

    private val _selectedCategoryName = MutableStateFlow<String?>(null)
    val selectedCategoryName: StateFlow<String?> = _selectedCategoryName.asStateFlow()

    // 페이지네이션 상태
    private var currentPage = 0
    private var allBooks = mutableListOf<SearchBookDto>()
    private var hasMoreData = true

    // 검색 기록
    private val _searchHistory = MutableStateFlow<List<SearchLogDto>>(emptyList())
    val searchHistory: StateFlow<List<SearchLogDto>> = _searchHistory.asStateFlow()

    // 검색 기록 저장 설정
    private val _searchHistoryEnabled = MutableStateFlow(true)
    val searchHistoryEnabled: StateFlow<Boolean> = _searchHistoryEnabled.asStateFlow()

    init {
        loadSearchHistory()
        loadSearchHistorySetting()
    }

    // 검색어 입력 시 호출
    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    // 정렬 타입 변경 (페이지네이션 리셋)
    fun changeSortType(newSortType: SortType) {
        if (_sortType.value != newSortType) {
            _sortType.value = newSortType
            searchBooks(reset = true)
        }
    }

    // 검색 실행 (페이지네이션 지원)
    fun searchBooks(reset: Boolean = true) {
        val currentQuery = _query.value
        if (currentQuery.isBlank()) return

        if (reset) {
            currentPage = 0
            allBooks.clear()
            hasMoreData = true
            _uiState.value = SearchUiState.Loading
        } else {
            // 추가 로드 - 로딩 상태 업데이트
            val currentState = _uiState.value
            if (currentState is SearchUiState.Success) {
                _uiState.value = currentState.copy(isLoadingMore = true)
            }
        }

        viewModelScope.launch {
            bookRepository.searchBooks(
                keyword = currentQuery,
                sortType = _sortType.value,
                page = currentPage,
                size = 20
            ).onSuccess { pageResponse ->
                allBooks.addAll(pageResponse.books)
                hasMoreData = !pageResponse.page.last
                currentPage++

                _uiState.value = SearchUiState.Success(
                    books = allBooks.toList(),
                    isLoadingMore = false,
                    hasMoreData = hasMoreData
                )

                // 검색 후 기록 새로고침 (첫 페이지만)
                if (reset) {
                    loadSearchHistory()
                }
            }.onFailure { exception ->
                if (reset) {
                    _uiState.value = SearchUiState.Error(
                        exception.message ?: "검색 중 오류가 발생했습니다."
                    )
                } else {
                    // 추가 로드 실패 - 기존 데이터 유지
                    val state = _uiState.value
                    if (state is SearchUiState.Success) {
                        _uiState.value = state.copy(isLoadingMore = false)
                    }
                }
            }
        }
    }

    // 다음 페이지 로드
    fun loadMore() {
        val currentState = _uiState.value
        if (currentState is SearchUiState.Success &&
            !currentState.isLoadingMore &&
            currentState.hasMoreData) {
            searchBooks(reset = false)
        }
    }

    // 검색 기록 로드
    fun loadSearchHistory() {
        viewModelScope.launch {
            bookRepository.getSearchHistory()
                .onSuccess { history ->
                    _searchHistory.value = history
                }
                .onFailure {
                    // 실패 시 무시 (빈 리스트 유지)
                }
        }
    }

    // 검색 기록 단일 삭제
    fun deleteSearchLog(id: Long) {
        viewModelScope.launch {
            bookRepository.deleteSearchLog(id)
                .onSuccess {
                    // 삭제 후 목록 새로고침
                    loadSearchHistory()
                }
        }
    }

    // 검색 기록 전체 삭제
    fun clearAllSearchHistory() {
        viewModelScope.launch {
            bookRepository.clearAllSearchHistory()
                .onSuccess {
                    _searchHistory.value = emptyList()
                }
        }
    }

    // 검색 기록 설정 로드
    private fun loadSearchHistorySetting() {
        viewModelScope.launch {
            bookRepository.getSearchHistorySetting()
                .onSuccess { enabled ->
                    _searchHistoryEnabled.value = enabled
                }
        }
    }

    // 검색 기록 설정 변경
    fun toggleSearchHistorySetting() {
        viewModelScope.launch {
            val newValue = !_searchHistoryEnabled.value
            bookRepository.updateSearchHistorySetting(newValue)
                .onSuccess { enabled ->
                    _searchHistoryEnabled.value = enabled
                }
        }
    }

    // 검색 기록 항목 클릭 시 해당 검색어로 검색 실행
    fun searchFromHistory(query: String) {
        _query.value = query
        searchBooks(reset = true)
    }

    /**
     * 초기 카테고리 설정 (네비게이션에서 전달받음)
     */
    fun setInitialCategory(categoryId: Long) {
        _selectedCategoryId.value = categoryId
        // 카테고리 이름 조회
        viewModelScope.launch {
            bookRepository.getAllCategories()
                .onSuccess { categories ->
                    val category = categories.find { it.id == categoryId }
                    _selectedCategoryName.value = category?.name
                    // 자동으로 검색 실행
                    searchBooksByCategory()
                }
                .onFailure { exception ->
                    android.util.Log.e("SearchViewModel", "카테고리 조회 실패", exception)
                }
        }
    }

    /**
     * 카테고리로 검색 (검색어 없이 베스트셀러 조회)
     */
    private fun searchBooksByCategory() {
        currentPage = 0
        allBooks.clear()
        hasMoreData = true
        _uiState.value = SearchUiState.Loading

        val categoryId = _selectedCategoryId.value ?: return

        viewModelScope.launch {
            // 카테고리별 베스트셀러 조회 API 사용
            bookRepository.getBestsellers(categoryId = categoryId.toInt())
                .onSuccess { books ->
                    // BookDto를 SearchBookDto로 변환
                    val searchBooks = books.map { book ->
                        SearchBookDto(
                            title = book.title,
                            isbn13 = book.isbn13,
                            author = book.author,
                            cover = book.cover,
                            description = book.description,
                            pubDate = null,
                            publisher = null,
                            priceSales = null,
                            priceStandard = null,
                            customerReviewRank = null,
                            ratingScore = null,
                            ratingCount = null,
                            link = null,
                            categoryIdList = listOf(categoryId.toInt())
                        )
                    }

                    allBooks.addAll(searchBooks)
                    hasMoreData = false  // 베스트셀러는 한 페이지만

                    _uiState.value = SearchUiState.Success(
                        books = allBooks.toList(),
                        isLoadingMore = false,
                        hasMoreData = hasMoreData
                    )
                }
                .onFailure { exception ->
                    _uiState.value = SearchUiState.Error(
                        exception.message ?: "검색 중 오류가 발생했습니다."
                    )
                }
        }
    }

    /**
     * 카테고리 필터 해제
     */
    fun clearCategoryFilter() {
        _selectedCategoryId.value = null
        _selectedCategoryName.value = null
        _query.value = ""
        _uiState.value = SearchUiState.Idle
    }
}