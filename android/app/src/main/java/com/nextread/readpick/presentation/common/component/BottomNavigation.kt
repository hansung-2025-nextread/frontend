package com.nextread.readpick.presentation.common.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nextread.readpick.R // R.drawable.ic_home, ic_library, ic_mypage 등을 사용하기 위함

/**
 * 하단 네비게이션 바 - 공통 컴포넌트로 분리
 */
@Composable
fun ReadPickBottomNavigation(
    // 현재 선택된 화면의 '경로' 또는 '탭'을 구분하는 식별자
    currentRoute: String,
    onHomeClick: () -> Unit,
    onMyLibraryClick: () -> Unit,
    onMyPageClick: () -> Unit
) {
    // HomeScreen에서 가져온 Surface, Row 레이아웃 유지
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(30.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home
            BottomNavItem(
                label = "홈",
                painter = painterResource(id = R.drawable.ic_home),
                isSelected = currentRoute == "home", // currentRoute를 통해 선택 상태 결정
                onClick = onHomeClick
            )
            // 내 서재
            BottomNavItem(
                label = "내 서재",
                painter = painterResource(id = R.drawable.ic_library),
                isSelected = currentRoute == "mylibrary",
                onClick = onMyLibraryClick
            )
            // 마이페이지
            BottomNavItem(
                label = "마이페이지",
                painter = painterResource(id = R.drawable.ic_mypage),
                isSelected = currentRoute == "mypage",
                onClick = onMyPageClick
            )
        }
    }
}


/**
 * 하단 네비게이션 아이템
 */
@Composable
fun BottomNavItem(
    label: String,
    painter: Painter,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painter,
            contentDescription = label,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}