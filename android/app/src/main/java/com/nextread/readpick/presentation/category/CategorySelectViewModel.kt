package com.nextread.readpick.presentation.category

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextread.readpick.data.model.category.CategoryDto
import com.nextread.readpick.domain.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 카테고리 선택 화면 UI 상태
 */
sealed class CategoryUiState {
    data object Loading : CategoryUiState()
    data class Success(val categoryGroups: Map<String, List<CategoryDto>>) : CategoryUiState()
    data class Error(val message: String) : CategoryUiState()
}

@HiltViewModel
class CategorySelectViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    /**
     * 카테고리 목록을 로드하고 장르별로 그룹화
     */
    private fun loadCategories() {
        Log.d(TAG, "카테고리 로드 시작")
        _uiState.value = CategoryUiState.Loading

        viewModelScope.launch {
            bookRepository.getAllCategories()
                .onSuccess { categories ->
                    Log.d(TAG, "카테고리 조회 성공: ${categories.size}개")
                    categories.take(5).forEach {
                        Log.d(TAG, "카테고리 샘플: ${it.name}")
                    }
                    val groupedCategories = groupCategoriesByGenre(categories)
                    Log.d(TAG, "그룹화 완료: ${groupedCategories.size}개 그룹")
                    groupedCategories.forEach { (key, value) ->
                        Log.d(TAG, "그룹: $key (${value.size}개)")
                    }
                    _uiState.value = CategoryUiState.Success(groupedCategories)
                }
                .onFailure { exception ->
                    Log.e(TAG, "카테고리 로드 실패", exception)
                    _uiState.value = CategoryUiState.Error(
                        exception.message ?: "카테고리를 불러올 수 없습니다"
                    )
                }
        }
    }

    /**
     * 카테고리를 장르별로 그룹화
     * 카테고리 이름에 "/"가 포함된 경우 첫 부분을 그룹명으로 사용
     * 예: "재테크/투자" -> "재테크" 그룹에 "투자"
     */
    private fun groupCategoriesByGenre(categories: List<CategoryDto>): Map<String, List<CategoryDto>> {
        // 1. 먼저 주요 장르별로 분류
        val genreMap = mutableMapOf<String, MutableList<CategoryDto>>()

        categories.forEach { category ->
            val genre = when {
                // IT/프로그래밍 관련
                category.name.contains("프로그래밍") ||
                category.name.contains("자바") ||
                category.name.contains("파이썬") ||
                category.name.contains("C++") ||
                category.name.contains("C") ||
                category.name.contains("컴퓨터") -> "IT/프로그래밍"

                // 경제/경영 관련
                category.name.contains("경제") ||
                category.name.contains("경영") ||
                category.name.contains("재테크") ||
                category.name.contains("투자") ||
                category.name.contains("마케팅") ||
                category.name.contains("창업") -> "경제/경영"

                // 자기계발 관련
                category.name.contains("자기계발") ||
                category.name.contains("리더십") ||
                category.name.contains("인간관계") ||
                category.name.contains("행복") ||
                category.name.contains("힐링") -> "자기계발"

                // 소설 관련
                category.name.contains("소설") -> "소설"

                // 에세이/시
                category.name.contains("에세이") ||
                category.name.contains("시") -> "에세이/시"

                // 인문학 관련
                category.name.contains("인문") ||
                category.name.contains("철학") ||
                category.name.contains("심리학") -> "인문학"

                // 역사 관련
                category.name.contains("역사") -> "역사"

                // 종교 관련
                category.name.contains("종교") ||
                category.name.contains("기독교") ||
                category.name.contains("가톨릭") ||
                category.name.contains("불교") ||
                category.name.contains("이슬람") ||
                category.name.contains("명상") -> "종교/명상"

                // 과학 관련
                category.name.contains("과학") -> "과학"

                // 사회과학 관련
                category.name.contains("사회") -> "사회과학"

                // 건강/취미 관련
                category.name.contains("건강") ||
                category.name.contains("요리") ||
                category.name.contains("다이어트") ||
                category.name.contains("헬스") ||
                category.name.contains("피트니스") ||
                category.name.contains("스포츠") -> "건강/취미"

                // 생활 관련
                category.name.contains("인테리어") ||
                category.name.contains("패션") ||
                category.name.contains("뷰티") ||
                category.name.contains("반려동물") -> "생활"

                // 여행 관련
                category.name.contains("여행") -> "여행"

                // 예술 관련
                category.name.contains("예술") ||
                category.name.contains("만화") -> "예술/만화"

                // 수험서/학습 관련
                category.name.contains("수험서") ||
                category.name.contains("공무원") ||
                category.name.contains("취업") ||
                category.name.contains("자격증") ||
                category.name.contains("공학") -> "수험/학습"

                // 청소년
                category.name.contains("청소년") -> "청소년"

                // 게임
                category.name.contains("게임") -> "게임"

                // 기타
                else -> "기타"
            }

            genreMap.getOrPut(genre) { mutableListOf() }.add(category)
        }

        return genreMap.toSortedMap()
    }

    companion object {
        private const val TAG = "CategorySelectViewModel"
    }
}
