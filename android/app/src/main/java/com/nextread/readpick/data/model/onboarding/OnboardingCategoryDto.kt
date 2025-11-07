package com.nextread.readpick.data.model.onboarding

import kotlinx.serialization.Serializable

/**
 * 온보딩 카테고리 DTO
 *
 * API: GET /v1/api/onboarding/categories
 * 온보딩 화면에 표시할 8개의 카테고리 목록을 가져옵니다.
 *
 * 8개 카테고리:
 * - 50917: 한국소설
 * - 55889: 에세이
 * - 336: 자기계발
 * - 170: 경제경영
 * - 51395: 심리학/정신분석학
 * - 169: 세계사 일반
 * - 987: 과학
 * - 1196: 여행
 */
@Serializable
data class OnboardingCategoryDto(
    /**
     * 카테고리 고유 ID (알라딘 API 카테고리 ID)
     */
    val categoryId: Int,

    /**
     * 카테고리 이름 (예: "한국소설", "에세이")
     */
    val name: String,

    /**
     * 카테고리 설명 (예: "한국 작가의 장편소설, 단편소설 등 문학 작품")
     */
    val description: String
)
