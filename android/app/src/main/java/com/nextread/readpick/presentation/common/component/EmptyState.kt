package com.nextread.readpick.presentation.common.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * 공통 빈 상태 컴포넌트
 *
 * 데이터가 없을 때 표시
 *
 * @param message 표시할 메시지
 * @param modifier Modifier
 *
 * 사용 예시:
 * ```kotlin
 * @Composable
 * fun MyPageScreen() {
 *     val savedBooks by viewModel.savedBooks.collectAsState()
 *
 *     if (savedBooks.isEmpty()) {
 *         EmptyState(message = "저장된 책이 없습니다")
 *     } else {
 *         BookList(books = savedBooks)
 *     }
 * }
 * ```
 */
@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(16.dp)
        )
    }
}
