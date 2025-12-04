package com.nextread.readpick.data.model.collection

import kotlinx.serialization.Serializable

/**
 * 컬렉션 이름 수정 요청
 *
 * 백엔드 API: PUT /api/users/me/collections/{collectionId}
 */
@Serializable
data class UpdateCollectionRequest(
    val name: String
)
