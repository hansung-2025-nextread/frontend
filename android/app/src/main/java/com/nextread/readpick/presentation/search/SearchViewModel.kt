package com.nextread.readpick.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextread.readpick.data.model.search.SearchLogDto
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

    // 검색 실행
    fun searchBooks() {
        val currentQuery = _query.value
        if (currentQuery.isBlank()) return

        _uiState.value = SearchUiState.Loading

        viewModelScope.launch {
            bookRepository.searchBooks(currentQuery)
                .onSuccess { books ->
                    _uiState.value = SearchUiState.Success(books)
                    // 검색 후 기록 새로고침
                    loadSearchHistory()
                }
                .onFailure { exception ->
                    _uiState.value = SearchUiState.Error(exception.message ?: "검색 중 오류가 발생했습니다.")
                }
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
        searchBooks()
    }
}