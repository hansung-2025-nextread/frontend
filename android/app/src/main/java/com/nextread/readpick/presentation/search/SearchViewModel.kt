package com.nextread.readpick.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                }
                .onFailure { exception ->
                    _uiState.value = SearchUiState.Error(exception.message ?: "검색 중 오류가 발생했습니다.")
                }
        }
    }
}