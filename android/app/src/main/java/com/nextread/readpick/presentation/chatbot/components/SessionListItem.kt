package com.nextread.readpick.presentation.chatbot.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nextread.readpick.data.model.chatbot.ChatSessionSummaryDto
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun SessionListItem(
    session: ChatSessionSummaryDto,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    // 삭제 확인 다이얼로그
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("대화 삭제") },
            text = { Text("이 대화를 삭제하시겠습니까?\n삭제된 대화는 복구할 수 없습니다.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete()
                }) {
                    Text("삭제", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("취소")
                }
            }
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 챗봇 아이콘
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatDateTime(session.updatedAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "삭제",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * ISO 8601 날짜 문자열을 "MM월 dd일 HH:mm" 형식으로 변환
 */
private fun formatDateTime(isoString: String): String {
    return try {
        val instant = Instant.parse(isoString)
        val formatter = DateTimeFormatter
            .ofPattern("MM월 dd일 HH:mm")
            .withZone(ZoneId.systemDefault())
        formatter.format(instant)
    } catch (e: Exception) {
        isoString // 파싱 실패 시 원본 반환
    }
}
