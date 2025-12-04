package com.nextread.readpick.presentation.collection

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextread.readpick.domain.repository.CollectionRepository
import com.nextread.readpick.data.remote.api.BookApi
import com.nextread.readpick.presentation.collection.components.UserCollection
import com.nextread.readpick.presentation.collection.components.FavoriteBookDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * CollectionViewModel
 *
 * 내 서재 화면의 상태를 관리하는 ViewModel입니다.
 * - 즐겨찾기한 책 목록 관리
 * - 사용자 정의 컬렉션(책장) 목록 관리
 */
@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val collectionRepository: CollectionRepository,
    private val bookApi: BookApi
) : ViewModel() {

    companion object {
        private const val TAG = "CollectionViewModel"
    }

    /**
     * UI 상태
     *
     * @param favoriteBookCount 즐겨찾기한 책의 개수
     * @param savedBooks 저장된(즐겨찾기한) 책 목록
     * @param userCollections 사용자가 만든 컬렉션(책장) 목록
     * @param isLoading 로딩 중 여부
     * @param error 에러 메시지
     */
    data class CollectionUiState(
        val favoriteBookCount: Int = 0,
        val savedBooks: List<FavoriteBookDto> = emptyList(),
        val userCollections: List<UserCollection> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    ) {
        // 컬렉션이 하나라도 있는지 여부
        val hasCustomCollections: Boolean
            get() = userCollections.isNotEmpty()
    }

    private val _uiState = MutableStateFlow(CollectionUiState())
    val uiState: StateFlow<CollectionUiState> = _uiState

    init {
        loadCollections()
        loadSavedBooks()
    }

    /**
     * 컬렉션 목록 로드
     *
     * Repository에서 실제 데이터를 가져옵니다.
     */
    private fun loadCollections() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            Log.d(TAG, "컬렉션 목록 로딩 시작...")

            try {
                // API 호출
                val collectionsResponse = collectionRepository.getCollections()

                // DTO를 도메인 모델로 변환
                val collections = collectionsResponse.map { dto ->
                    UserCollection(
                        id = dto.id,
                        name = dto.name,
                        bookCount = dto.bookCount.toInt(),
                        latestCoverUrl = null // API에서 제공하지 않음
                    )
                }

                _uiState.update {
                    it.copy(
                        userCollections = collections,
                        isLoading = false
                    )
                }
                Log.d(TAG, "✅ 컬렉션 목록 로드 성공: ${collections.size}개")
            } catch (e: Exception) {
                Log.e(TAG, "❌ 컬렉션 목록 로드 실패", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "컬렉션을 불러올 수 없습니다"
                    )
                }
            }
        }
    }

    /**
     * 저장된 책 목록 로드 (즐겨찾기한 책)
     *
     * API에서 저장된 책 목록을 가져와서 UI 상태를 업데이트합니다.
     */
    private fun loadSavedBooks() {
        viewModelScope.launch {
            Log.d(TAG, "저장된 책 목록 로딩 시작...")

            try {
                // API 호출
                val savedBooksResponse = bookApi.getSavedBooks(page = 0, size = 100)

                // DTO를 FavoriteBookDto로 변환
                val savedBooks = savedBooksResponse.content.map { dto ->
                    FavoriteBookDto(
                        isbn13 = dto.isbn13,
                        title = dto.title,
                        author = dto.author,
                        coverUrl = dto.cover
                    )
                }

                _uiState.update {
                    it.copy(
                        savedBooks = savedBooks,
                        favoriteBookCount = savedBooks.size
                    )
                }
                Log.d(TAG, "✅ 저장된 책 목록 로드 성공: ${savedBooks.size}권")
            } catch (e: Exception) {
                Log.e(TAG, "❌ 저장된 책 목록 로드 실패", e)
                // 에러가 발생해도 다른 기능은 동작하도록 에러 상태는 업데이트하지 않음
            }
        }
    }

    /**
     * 컬렉션 추가
     *
     * @param name 컬렉션 이름
     * @param bookIsbnList 컬렉션에 추가할 책 ISBN 목록
     */
    fun addCollection(name: String, bookIsbnList: List<String>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            Log.d(TAG, "========================================")
            Log.d(TAG, "🚀 컬렉션 추가 시작")
            Log.d(TAG, "   - 이름: $name")
            Log.d(TAG, "   - 책 개수: ${bookIsbnList.size}권")
            Log.d(TAG, "   - ISBN 목록: $bookIsbnList")
            Log.d(TAG, "   - 현재 컬렉션 개수: ${_uiState.value.userCollections.size}")

            try {
                // API 호출
                Log.d(TAG, "📡 API 호출 중...")
                val newCollectionResponse = collectionRepository.createCollection(name, bookIsbnList)
                Log.d(TAG, "✅ API 응답 받음: id=${newCollectionResponse.id}, name=${newCollectionResponse.name}, bookCount=${newCollectionResponse.bookCount}")

                // DTO를 도메인 모델로 변환
                val newCollection = UserCollection(
                    id = newCollectionResponse.id,
                    name = newCollectionResponse.name,
                    bookCount = newCollectionResponse.bookCount.toInt(),
                    latestCoverUrl = null
                )
                Log.d(TAG, "🔄 도메인 모델 변환 완료: $newCollection")

                _uiState.update {
                    it.copy(
                        userCollections = it.userCollections + newCollection,
                        isLoading = false
                    )
                }
                Log.d(TAG, "✅ UI 상태 업데이트 완료")
                Log.d(TAG, "   - 업데이트 후 컬렉션 개수: ${_uiState.value.userCollections.size}")
                Log.d(TAG, "   - 컬렉션 목록: ${_uiState.value.userCollections.map { it.name }}")
                Log.d(TAG, "========================================")
            } catch (e: Exception) {
                Log.e(TAG, "❌ 컬렉션 추가 실패", e)
                Log.e(TAG, "   - 에러 메시지: ${e.message}")
                Log.e(TAG, "   - 스택트레이스:")
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "컬렉션을 추가할 수 없습니다"
                    )
                }
                Log.d(TAG, "========================================")
            }
        }
    }

    /**
     * 컬렉션 삭제
     *
     * @param collectionIds 삭제할 컬렉션 ID 목록
     */
    fun deleteCollections(collectionIds: List<Long>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            Log.d(TAG, "컬렉션 삭제 시작: ${collectionIds.size}개")

            try {
                // API 호출 - 각 컬렉션을 개별적으로 삭제
                collectionIds.forEach { id ->
                    collectionRepository.deleteCollection(id)
                }

                // 로컬 상태에서 제거
                _uiState.update {
                    it.copy(
                        userCollections = it.userCollections.filter { collection ->
                            collection.id !in collectionIds
                        },
                        isLoading = false
                    )
                }
                Log.d(TAG, "✅ 컬렉션 삭제 성공")
            } catch (e: Exception) {
                Log.e(TAG, "❌ 컬렉션 삭제 실패", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "컬렉션을 삭제할 수 없습니다"
                    )
                }
            }
        }
    }

    /**
     * 컬렉션 이름 변경
     *
     * @param collectionId 컬렉션 ID
     * @param newName 새로운 이름
     */
    fun renameCollection(collectionId: Long, newName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            Log.d(TAG, "컬렉션 이름 변경 시작: ID=$collectionId, 새 이름=$newName")

            try {
                // API 호출
                val updatedCollectionResponse = collectionRepository.renameCollection(collectionId, newName)

                // 로컬 상태 업데이트
                _uiState.update {
                    it.copy(
                        userCollections = it.userCollections.map { collection ->
                            if (collection.id == collectionId) {
                                collection.copy(name = updatedCollectionResponse.name)
                            } else {
                                collection
                            }
                        },
                        isLoading = false
                    )
                }
                Log.d(TAG, "✅ 컬렉션 이름 변경 성공")
            } catch (e: Exception) {
                Log.e(TAG, "❌ 컬렉션 이름 변경 실패", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "컬렉션 이름을 변경할 수 없습니다"
                    )
                }
            }
        }
    }

    /**
     * 기존 컬렉션에 책 추가
     *
     * @param collectionId 컬렉션 ID
     * @param isbn13List 추가할 책의 ISBN 목록
     */
    fun addBooksToCollection(collectionId: Long, isbn13List: List<String>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            Log.d(TAG, "컬렉션에 책 추가 시작: 컬렉션 ID=$collectionId, 책 개수=${isbn13List.size}권")

            try {
                // 각 책을 개별적으로 추가
                isbn13List.forEach { isbn13 ->
                    collectionRepository.addBookToCollection(collectionId, isbn13)
                }

                _uiState.update { it.copy(isLoading = false) }
                Log.d(TAG, "✅ 컬렉션에 책 추가 성공: ${isbn13List.size}권")
            } catch (e: Exception) {
                Log.e(TAG, "❌ 컬렉션에 책 추가 실패", e)
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
     * 즐겨찾기 책 삭제
     *
     * @param isbn13List 삭제할 책의 ISBN 목록
     *
     * TODO: API 연동 시 Repository를 통해 서버에서 즐겨찾기 해제
     */
    fun deleteFavoriteBooks(isbn13List: List<String>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            Log.d(TAG, "즐겨찾기 삭제 시작: ${isbn13List.size}권")

            try {
                // TODO: API 호출
                // favoriteRepository.removeFavorites(isbn13List)

                // 임시: favoriteBookCount만 업데이트
                _uiState.update {
                    it.copy(
                        favoriteBookCount = maxOf(0, it.favoriteBookCount - isbn13List.size),
                        isLoading = false
                    )
                }
                Log.d(TAG, "✅ 즐겨찾기 삭제 성공")
            } catch (e: Exception) {
                Log.e(TAG, "❌ 즐겨찾기 삭제 실패", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "즐겨찾기를 해제할 수 없습니다"
                    )
                }
            }
        }
    }
}
