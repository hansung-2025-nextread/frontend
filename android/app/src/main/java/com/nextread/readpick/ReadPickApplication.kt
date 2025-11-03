package com.nextread.readpick

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * ReadPick 애플리케이션 클래스
 *
 * @HiltAndroidApp: Hilt 의존성 주입을 위한 필수 어노테이션
 * - 앱이 시작될 때 Hilt 컴포넌트를 초기화
 * - 모든 DI 모듈의 시작점
 * - 이 어노테이션이 없으면 Hilt가 작동하지 않음
 *
 * 역할:
 * 1. Hilt 초기화
 * 2. 전역 설정 (필요시)
 * 3. 앱 생명주기 관리
 */
@HiltAndroidApp
class ReadPickApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Hilt가 자동으로 초기화됨
        // 필요한 다른 초기화 코드가 있으면 여기에 추가
    }
}
