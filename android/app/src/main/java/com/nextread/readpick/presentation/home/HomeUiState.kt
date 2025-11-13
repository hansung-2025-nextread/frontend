package com.nextread.readpick.presentation.home

import com.nextread.readpick.data.model.book.BookDto

/**
 * 홈 화면의 UI 상태를 나타내는 Sealed Interface
 */
sealed interface HomeUiState {
    /**
     * 로딩 중
     */
    data object Loading : HomeUiState

    /**
     * 성공
     * @param books List<String>에서 List<BookDto>로 변경
     */
    data class Success(val books: List<BookDto>) : HomeUiState

    /**
     * 에러
     */
    data class Error(val message: String) : HomeUiState
}