package com.nextread.readpick.presentation.search

import com.nextread.readpick.data.model.search.SearchBookDto

sealed interface SearchUiState {
    data object Idle : SearchUiState // 초기 상태 (아무것도 안 함)
    data object Loading : SearchUiState // 로딩 중
    data class Success(
        val books: List<SearchBookDto>,
        val isLoadingMore: Boolean = false,
        val hasMoreData: Boolean = true
    ) : SearchUiState // 검색 성공
    data class Error(val message: String) : SearchUiState // 에러
}