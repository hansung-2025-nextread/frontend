package com.nextread.readpick.presentation.chatbot.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.nextread.readpick.data.model.chatbot.ChatMessageDto
import com.nextread.readpick.data.model.chatbot.MessageRole

@Composable
fun ChatBubble(
    message: ChatMessageDto,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (message.role) {
        MessageRole.USER -> {
            // 사용자 메시지 - 오른쪽 정렬
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom
            ) {
                Surface(
                    modifier = Modifier
                        .widthIn(max = 280.dp)
                        .shadow(2.dp, RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)),
                    shape = RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = message.content,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }
        }
        MessageRole.ASSISTANT -> {
            // AI 응답 - 왼쪽 정렬
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Top
            ) {
                // AI 아바타
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.size(8.dp))

                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Surface(
                        modifier = Modifier
                            .widthIn(max = 280.dp)
                            .shadow(2.dp, RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)),
                        shape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = message.content,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }

                    // 책 추천 카드 (있는 경우만)
                    if (!message.books.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        BookRecommendationCard(
                            books = message.books,
                            onBookClick = onBookClick
                        )
                    }
                }
            }
        }
    }
}
