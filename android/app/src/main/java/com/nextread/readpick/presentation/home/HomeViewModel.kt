package com.nextread.readpick.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextread.readpick.data.local.TokenManager
import com.nextread.readpick.data.model.book.BookDto
import com.nextread.readpick.domain.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _personalizedBooks = MutableStateFlow<List<BookDto>>(emptyList())
    val personalizedBooks: StateFlow<List<BookDto>> = _personalizedBooks.asStateFlow()

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    init {
        loadHomeData()
    }

    /**
     * 홈 화면 데이터 로드 (베스트셀러 + 개인화 추천)
     */
    private fun loadHomeData() {
        _uiState.value = HomeUiState.Loading

        viewModelScope.launch {
            try {
                // 사용자 이름 조회
                _userName.value = tokenManager.getUserNameFlow().first()

                // 병렬로 API 호출
                val bestsellerDeferred = async { bookRepository.getBestsellers() }
                val recommendationDeferred = async { bookRepository.getPersonalizedRecommendations(15) }

                // 베스트셀러 결과 처리 (필수)
                bestsellerDeferred.await()
                    .onSuccess { books ->
                        _uiState.value = HomeUiState.Success(books)
                    }
                    .onFailure { exception ->
                        _uiState.value = HomeUiState.Error(exception.message ?: "알 수 없는 오류")
                        return@launch
                    }

                // 개인화 추천 결과 처리 (선택)
                recommendationDeferred.await()
                    .onSuccess { books ->
                        _personalizedBooks.value = books
                    }
                    .onFailure { exception ->
                        Log.e(TAG, "개인화 추천 로드 실패", exception)
                        _personalizedBooks.value = emptyList()
                    }
            } catch (e: Exception) {
                Log.e(TAG, "loadHomeData 에러", e)
                _uiState.value = HomeUiState.Error(e.message ?: "알 수 없는 오류")
            }
        }
    }

    /**
     * 베스트셀러 목록을 로드하는 함수 (호환성 유지)
     */
    fun loadBestsellers(categoryId: Int? = null) {
        _uiState.value = HomeUiState.Loading

        viewModelScope.launch {
            bookRepository.getBestsellers(categoryId = categoryId)
                .onSuccess { books ->
                    _uiState.value = HomeUiState.Success(books)
                }
                .onFailure { exception ->
                    _uiState.value = HomeUiState.Error(exception.message ?: "알 수 없는 오류 발생")
                }
        }
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}