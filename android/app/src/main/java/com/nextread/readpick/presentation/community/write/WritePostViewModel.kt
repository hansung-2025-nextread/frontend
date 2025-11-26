package com.nextread.readpick.presentation.community.write

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextread.readpick.data.model.book.SavedBookDto
import com.nextread.readpick.data.model.community.CommunityCategoryDto
import com.nextread.readpick.data.model.community.CommunityBookDto
import com.nextread.readpick.data.model.community.CreatePostRequest
import android.util.Log
import com.nextread.readpick.domain.repository.BookRepository
import com.nextread.readpick.domain.repository.CommunityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WritePostViewModel @Inject constructor(
    private val communityRepository: CommunityRepository,
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WritePostUiState>(WritePostUiState.Loading)
    val uiState: StateFlow<WritePostUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent.asSharedFlow()

    private var categories: List<CommunityCategoryDto> = emptyList()
    private var savedBooks: List<SavedBookDto> = emptyList()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // 카테고리와 내 서재 데이터를 병렬로 로드
            val categoriesDeferred = async { communityRepository.getCategories() }
            val savedBooksDeferred = async { bookRepository.getSavedBooks() }

            val categoriesResult = categoriesDeferred.await()
            val savedBooksResult = savedBooksDeferred.await()

            categoriesResult.onSuccess { categoryList ->
                categories = categoryList
                savedBooksResult.onSuccess { bookList ->
                    savedBooks = bookList
                    Log.d(TAG, "내 서재 책 로드 성공: ${bookList.size}권")
                }.onFailure { exception ->
                    Log.e(TAG, "내 서재 책 로드 실패", exception)
                    savedBooks = emptyList()
                }
                _uiState.value = WritePostUiState.Ready(
                    categories = categoryList,
                    savedBooks = savedBooks
                )
            }.onFailure { exception ->
                _uiState.value = WritePostUiState.Error(
                    exception.message ?: "데이터를 불러올 수 없습니다"
                )
            }
        }
    }

    fun selectCategory(categoryId: Long) {
        val currentState = _uiState.value
        if (currentState is WritePostUiState.Ready) {
            _uiState.value = currentState.copy(selectedCategoryId = categoryId)
        }
    }

    fun selectPostType(postType: PostType) {
        val currentState = _uiState.value
        if (currentState is WritePostUiState.Ready) {
            _uiState.value = currentState.copy(selectedPostType = postType)
        }
    }

    fun updateTitle(title: String) {
        val currentState = _uiState.value
        if (currentState is WritePostUiState.Ready) {
            _uiState.value = currentState.copy(title = title)
        }
    }

    fun updateContent(content: String) {
        val currentState = _uiState.value
        if (currentState is WritePostUiState.Ready) {
            _uiState.value = currentState.copy(content = content)
        }
    }

    fun selectBook(book: CommunityBookDto?) {
        val currentState = _uiState.value
        if (currentState is WritePostUiState.Ready) {
            _uiState.value = currentState.copy(selectedBook = book)
        }
    }

    fun submitPost() {
        val currentState = _uiState.value
        if (currentState !is WritePostUiState.Ready) return

        // 유효성 검사
        if (currentState.selectedCategoryId == null) {
            _uiState.value = currentState.copy(errorMessage = "카테고리를 선택해주세요")
            return
        }
        if (currentState.selectedPostType == null) {
            _uiState.value = currentState.copy(errorMessage = "게시물 타입을 선택해주세요")
            return
        }
        if (currentState.title.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "제목을 입력해주세요")
            return
        }
        if (currentState.content.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "내용을 입력해주세요")
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isSubmitting = true, errorMessage = null)

            val request = CreatePostRequest(
                categoryId = currentState.selectedCategoryId,
                postType = currentState.selectedPostType.value,
                title = currentState.title,
                content = currentState.content,
                bookIsbn13 = currentState.selectedBook?.isbn13
            )

            communityRepository.createPost(request)
                .onSuccess { post ->
                    _uiState.value = WritePostUiState.Success(post.id)
                    _navigationEvent.emit(NavigationEvent.PostCreated(post.id))
                }
                .onFailure { exception ->
                    _uiState.value = currentState.copy(
                        isSubmitting = false,
                        errorMessage = exception.message ?: "게시물을 작성할 수 없습니다"
                    )
                }
        }
    }

    fun clearError() {
        val currentState = _uiState.value
        if (currentState is WritePostUiState.Ready) {
            _uiState.value = currentState.copy(errorMessage = null)
        }
    }

    sealed class NavigationEvent {
        data class PostCreated(val postId: Long) : NavigationEvent()
    }

    companion object {
        private const val TAG = "WritePostViewModel"
    }
}
