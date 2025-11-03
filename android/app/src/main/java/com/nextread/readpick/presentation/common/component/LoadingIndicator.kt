package com.nextread.readpick.presentation.common.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * 공통 로딩 표시 컴포넌트
 *
 * API 호출 중일 때 표시
 *
 * 사용 예시:
 * ```kotlin
 * @Composable
 * fun BookDetailScreen() {
 *     val uiState by viewModel.uiState.collectAsState()
 *
 *     when (uiState) {
 *         is UiState.Loading -> LoadingIndicator()
 *         is UiState.Success -> BookContent(...)
 *         is UiState.Error -> ErrorMessage(...)
 *     }
 * }
 * ```
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
