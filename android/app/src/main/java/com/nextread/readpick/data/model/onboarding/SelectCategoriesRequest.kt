package com.nextread.readpick.data.model.onboarding

import kotlinx.serialization.Serializable

/**
 * 카테고리 선택 요청 DTO
 *
 * API: POST /v1/api/onboarding/select-categories
 * 사용자가 선택한 카테고리 ID 목록을 서버에 전송합니다.
 *
 * 사용 시나리오:
 * 1. 사용자가 카테고리를 1개 이상 선택하고 "완료" 버튼 클릭
 *    → categoryIds = [50917, 336, 987]
 *
 * 2. 사용자가 "건너뛰기" 버튼 클릭
 *    → categoryIds = [] (빈 배열)
 */
@Serializable
data class SelectCategoriesRequest(
    /**
     * 선택한 카테고리 ID 목록
     * - 빈 배열 가능 (건너뛰기)
     * - 각 카테고리는 가중치 1.0으로 사용자 선호도에 저장됨
     */
    val categoryIds: List<Int>
)
