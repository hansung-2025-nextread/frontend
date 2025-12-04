package com.nextread.readpick.presentation.collection.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextread.readpick.domain.repository.CollectionRepository
import com.nextread.readpick.data.model.collection.CollectionBookResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 컬렉션 상세 화면 ViewModel
 *
 * 컬렉션에 담긴 책 목록을 관리하고,
 * 책 추가/삭제 기능을 제공합니다.
 */
@HiltViewModel
class CollectionDetailViewModel @Inject constructor(
    private val collectionRepository: CollectionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val TAG = "CollectionDetailVM"
    }

    // Navigation arguments에서 collectionId 가져오기
    private val collectionId: Long = savedStateHandle.get<String>("collectionId")?.toLongOrNull() ?: 0L

    /**
     * UI 상태
     *
     * @param collectionName 컬렉션 이름
     * @param books 컬렉션 내 책 목록
     * @param isLoading 로딩 중 여부
     * @param error 에러 메시지
     * @param isEditMode 편집 모드 여부
     * @param selectedBookIds 선택된 책 ID 목록 (편집 모드에서 사용)
     */
    data class CollectionDetailUiState(
        val collectionName: String = "",
        val books: List<CollectionBookResponse> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val isEditMode: Boolean = false,
        val selectedBookIds: Set<String> = emptySet()
    ) {
        val hasBooks: Boolean
            get() = books.isNotEmpty()

        val selectedCount: Int
            get() = selectedBookIds.size
    }

    private val _uiState = MutableStateFlow(CollectionDetailUiState())
    val uiState: StateFlow<CollectionDetailUiState> = _uiState

    init {
        loadCollectionBooks()
    }

    /**
     * 컬렉션 내 책 목록 로드
     */
    private fun loadCollectionBooks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            Log.d(TAG, "컬렉션 책 목록 로딩 시작: collectionId=$collectionId")

            try {
                // 페이지 응답에서 content만 가져오기
                val pageResponse = collectionRepository.getBooksInCollection(collectionId)
                val books = pageResponse.content

                _uiState.update {
                    it.copy(
                        books = books,
                        isLoading = false
                    )
                }
                Log.d(TAG, "✅ 책 목록 로드 성공: ${books.size}권")
            } catch (e: Exception) {
                Log.e(TAG, "❌ 책 목록 로드 실패", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "책 목록을 불러올 수 없습니다"
                    )
                }
            }
        }
    }

    /**
     * 컬렉션에 책 추가
     *
     * @param isbn13 추가할 책의 ISBN
     */
    fun addBookToCollection(isbn13: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            Log.d(TAG, "책 추가 시작: isbn=$isbn13")

            try {
                collectionRepository.addBookToCollection(collectionId, isbn13)

                // 목록 다시 로드
                loadCollectionBooks()
                Log.d(TAG, "✅ 책 추가 성공")
            } catch (e: Exception) {
                Log.e(TAG, "❌ 책 추가 실패", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "책을 추가할 수 없습니다"
                    )
                }
            }
        }
    }

    /**
     * 편집 모드 토글
     */
    fun toggleEditMode() {
        _uiState.update {
            it.copy(
                isEditMode = !it.isEditMode,
                selectedBookIds = emptySet() // 편집 모드 전환 시 선택 초기화
            )
        }
        Log.d(TAG, "편집 모드: ${_uiState.value.isEditMode}")
    }

    /**
     * 책 선택/해제 (편집 모드에서)
     *
     * @param isbn13 선택할 책의 ISBN
     */
    fun toggleBookSelection(isbn13: String) {
        _uiState.update {
            val newSelected = if (isbn13 in it.selectedBookIds) {
                it.selectedBookIds - isbn13
            } else {
                it.selectedBookIds + isbn13
            }
            it.copy(selectedBookIds = newSelected)
        }
    }

    /**
     * 선택된 책들 삭제
     */
    fun deleteSelectedBooks() {
        viewModelScope.launch {
            val selectedIds = _uiState.value.selectedBookIds
            if (selectedIds.isEmpty()) return@launch

            _uiState.update { it.copy(isLoading = true, error = null) }
            Log.d(TAG, "책 삭제 시작: ${selectedIds.size}권")

            try {
                // 각 책을 개별적으로 삭제
                selectedIds.forEach { isbn13 ->
                    collectionRepository.removeBookFromCollection(collectionId, isbn13)
                }

                // 편집 모드 종료 및 목록 다시 로드
                _uiState.update { it.copy(isEditMode = false, selectedBookIds = emptySet()) }
                loadCollectionBooks()
                Log.d(TAG, "✅ 책 삭제 성공")
            } catch (e: Exception) {
                Log.e(TAG, "❌ 책 삭제 실패", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "책을 삭제할 수 없습니다"
                    )
                }
            }
        }
    }

    /**
     * 컬렉션 이름 설정 (NavGraph에서 전달받은 경우)
     */
    fun setCollectionName(name: String) {
        _uiState.update { it.copy(collectionName = name) }
    }
}