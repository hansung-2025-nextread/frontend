package com.nextread.readpick

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.nextread.readpick.presentation.auth.login.LoginScreen
import com.nextread.readpick.ui.theme.NextReadTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity
 *
 * 앱의 진입점
 *
 * - Hilt 의존성 주입 활성화 (@AndroidEntryPoint)
 * - 로그인 여부에 따라 화면 분기
 *   - 비로그인: LoginScreen
 *   - 로그인: HomeScreen (TODO: 추후 구현)
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NextReadTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 로그인 상태 관리 (추후 DataStore로 개선)
                    var isLoggedIn by remember { mutableStateOf(false) }

                    if (isLoggedIn) {
                        // TODO: HomeScreen으로 변경
                        HomeScreenPlaceholder()
                    } else {
                        LoginScreen(
                            onLoginSuccess = {
                                isLoggedIn = true
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 임시 홈 화면
 *
 * TODO: 실제 HomeScreen 구현 후 교체
 */
@Composable
fun HomeScreenPlaceholder() {
    Text(
        text = "🎉 로그인 성공!\n\nHomeScreen 구현 예정",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        textAlign = TextAlign.Center
    )
}