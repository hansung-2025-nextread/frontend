package com.nextread.readpick.domain.usecase.user

import com.nextread.readpick.data.model.user.UserInfoDto
import com.nextread.readpick.domain.repository.AuthRepository // 🚨 AuthRepository 사용
import javax.inject.Inject

/**
 * 사용자 프로필 정보를 로컬에서 가져오는 Use Case
 */
class GetUserInfoUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): UserInfoDto? { // 🚨 suspend 제거 및 invoke 연산자 오버로딩
        return authRepository.getUserInfo()
    }
}