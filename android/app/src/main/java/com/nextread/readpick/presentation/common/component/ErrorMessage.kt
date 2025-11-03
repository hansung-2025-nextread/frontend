package com.nextread.readpick.presentation.common.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * 공통 에러 메시지 컴포넌트
 *
 * API 호출 실패 시 표시
 *
 * @param message 에러 메시지
 * @param onRetry 재시도 버튼 클릭 이벤트 (null이면 버튼 표시 안 함)
 * @param modifier Modifier
 *
 * 사용 예시:
 * ```kotlin
 * @Composable
 * fun BookDetailScreen(viewModel: BookDetailViewModel) {
 *     val uiState by viewModel.uiState.collectAsState()
 *
 *     when (val state = uiState) {
 *         is UiState.Error -> ErrorMessage(
 *             message = state.message,
 *             onRetry = { viewModel.loadBook() }
 *         )
 *     }
 * }
 * ```
 */
@Composable
fun ErrorMessage(
    message: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            // 에러 메시지
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.error
            )

            // 재시도 버튼 (onRetry가 null이 아닐 때만)
            if (onRetry != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onRetry) {
                    Text("재시도")
                }
            }
        }
    }
}
