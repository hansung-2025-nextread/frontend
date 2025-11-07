package com.nextread.readpick

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.nextread.readpick.presentation.navigation.ReadPickNavGraph
import com.nextread.readpick.ui.theme.NextReadTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity
 *
 * 앱의 진입점
 *
 * - Hilt 의존성 주입 활성화 (@AndroidEntryPoint)
 * - Navigation Compose를 사용한 화면 전환 관리
 * - 초기 화면: Login → Onboarding (필요시) → Home
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
                    // NavController 생성
                    val navController = rememberNavController()

                    // Navigation Graph 설정
                    ReadPickNavGraph(navController = navController)
                }
            }
        }
    }
}