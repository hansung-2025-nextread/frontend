package com.nextread.readpick.data.model.admin

import kotlinx.serialization.Serializable

/**
 * 사용자 정지 요청
 */
@Serializable
data class SuspendUserRequest(
    val reason: String
)
