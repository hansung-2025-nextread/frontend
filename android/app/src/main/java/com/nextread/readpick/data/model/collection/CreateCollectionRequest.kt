package com.nextread.readpick.data.model.collection

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class CreateCollectionRequest(
    val name: String,
    // 컬렉션 생성 시 추가할 책의 ISBN 목록 (선택 사항)
    val isbns: List<String> = emptyList()
)