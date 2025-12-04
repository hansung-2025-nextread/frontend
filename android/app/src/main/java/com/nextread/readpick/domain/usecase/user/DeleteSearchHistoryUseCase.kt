// app/src/main/java/com/nextread/readpick/domain/usecase/user/DeleteSearchHistoryUseCase.kt

package com.nextread.readpick.domain.usecase.user

import javax.inject.Inject

/**
 * 검색 기록 삭제를 담당하는 Use Case.
 */
class DeleteSearchHistoryUseCase @Inject constructor(
    // private val searchRepository: SearchRepository // TODO: Repository 의존성 주입 (필요시)
) {
    /**
     * 검색 기록 삭제 로직을 실행합니다.
     */
    suspend fun execute() {
        // TODO: 로컬 데이터베이스에서 검색 기록을 삭제하는 로직 구현
        println("검색 기록 삭제 실행됨") // 임시 로그
    }
}