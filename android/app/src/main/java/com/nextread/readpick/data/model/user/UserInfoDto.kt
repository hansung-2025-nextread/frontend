// 🚨🚨🚨 @file: 어노테이션은 반드시 package 선언보다 앞에 위치해야 합니다. 🚨🚨🚨
@file:Suppress("ClassName")package com.nextread.readpick.data.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 사용자 프로필 정보를 나타내는 DTO (Data Transfer Object)
 * Kotlinx Serialization 표준을 따름.
 */
@Serializable
data class UserInfoDto(
    // 사용자 이름
    @SerialName("name") val name: String,

    // 사용자 이메일
    @SerialName("email") val email: String,

    // 사용자 프로필 이미지 URL
    @SerialName("picture") val profileImageUrl: String?,
)
