package com.nextread.readpick.presentation.home

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
class HomeViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // 🚨 categoryId = null (기본값)로 베스트셀러 로드
        loadBestsellers(categoryId = 50917)
    }

    /**
     * 베스트셀러 목록을 로드하는 함수
     * 🚨 categoryId: Int? 파라미터 추가
     */
    fun loadBestsellers(categoryId: Int? = null) {
        _uiState.value = HomeUiState.Loading

        viewModelScope.launch {
            // 🚨 categoryId 파라미터 전달
            bookRepository.getBestsellers(categoryId = categoryId)
                .onSuccess { books ->
                    _uiState.value = HomeUiState.Success(books)
                }
                .onFailure { exception ->
                    _uiState.value = HomeUiState.Error(exception.message ?: "알 수 없는 오류 발생")
                }
        }
    }
}