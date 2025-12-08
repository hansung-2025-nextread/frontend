package com.nextread.readpick.presentation.mypage.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun MyPageHeader(
    name: String,
    email: String,
    profileImageUrl: String?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 사용자 프로필 이미지
        if (!profileImageUrl.isNullOrEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(profileImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "User Profile Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        } else {
            // 플레이스홀더
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(Color.LightGray.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
            )
        }


        Spacer(modifier = Modifier.width(16.dp))

        // 이름 및 이메일
        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}