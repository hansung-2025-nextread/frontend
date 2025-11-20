import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // Hilt - 의존성 주입 (ViewModel, Repository 자동 생성)
    alias(libs.plugins.hilt.android)
    // Kotlinx Serialization - JSON 파싱 (백엔드 API 응답 처리)
    alias(libs.plugins.kotlin.serialization)
    // kapt - Annotation Processing (Hilt 컴파일러)
    kotlin("kapt")
    // Google Services - Firebase 설정 (google-services.json 처리)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.nextread.readpick"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.nextread.readpick"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // local.properties에서 BASE_URL 읽기
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(FileInputStream(localPropertiesFile))
        }

        // BASE_URL을 BuildConfig에 추가 (없으면 기본값 사용)
        val baseUrl = localProperties.getProperty("BASE_URL") ?: "https://api.readpick.com/"
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true  // BuildConfig 활성화
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // Android Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Retrofit - REST API 통신 (백엔드 /api/* 호출)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)

    // OkHttp - HTTP 클라이언트 (JWT 토큰 자동 추가, 로깅)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // Hilt - 의존성 주입 (Repository, ViewModel, API 자동 생성 및 주입)
    implementation(libs.hilt.android)
    implementation(libs.androidx.compose.foundation)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // DataStore - 로컬 저장소 (JWT 토큰, 로그인 상태 유지)
    implementation(libs.datastore.preferences)

    // Coil - 이미지 로딩 (책 표지 URL -> 이미지)
    implementation(libs.coil.compose)

    // Navigation - 화면 전환 (로그인 -> 홈 -> 도서 상세 등)
    implementation(libs.navigation.compose)

    // Kotlinx Serialization - JSON 파싱 (API 응답 JSON -> Kotlin 객체)
    implementation(libs.kotlinx.serialization.json)

    // Coroutines - 비동기 처리 (API 호출 시 UI 블로킹 방지)
    implementation(libs.kotlinx.coroutines.android)

    // Firebase - Google 로그인 인증
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)

    // Credentials Manager - Google One Tap 로그인 (최신 방식)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}