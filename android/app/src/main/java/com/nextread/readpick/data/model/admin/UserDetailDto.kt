package com.nextread.readpick.data.model.admin

import kotlinx.serialization.Serializable

/**
 * 사용자 상세 정보 (관리자용)
 * 백엔드 AdminUserResponse와 매핑
 */
@Serializable
data class UserDetailDto(
    val id: Long,
    val email: String,
    val name: String,
    val picture: String?,
    val provider: String,
    val role: String,
    val suspended: Boolean,
    val suspendedAt: String?,
    val suspensionReason: String?,
    val createdAt: String
) {
    // 편의 프로퍼티
    val userId: Long get() = id
    val isSuspended: Boolean get() = suspended
    val suspendedReason: String? get() = suspensionReason
}
