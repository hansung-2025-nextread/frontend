// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    // Hilt - 의존성 주입 플러그인
    alias(libs.plugins.hilt.android) apply false
    // Kotlinx Serialization - JSON 직렬화 플러그인
    alias(libs.plugins.kotlin.serialization) apply false
    // Google Services - Firebase 설정
    alias(libs.plugins.google.services) apply false
}