package com.nextread.readpick.data.model.collection

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class CreateCollectionRequest(
    val name: String
    // 백엔드 API 문서에 따르면 컬렉션 생성 시 name만 필요
    // 책은 생성 후 addBookToCollection으로 개별 추가
)