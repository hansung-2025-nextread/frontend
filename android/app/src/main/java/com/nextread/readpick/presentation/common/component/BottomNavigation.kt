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
import com.nextread.readpick.R

/**
 * 하단 네비게이션 바 - 공통 컴포넌트
 *
 * 모든 화면에서 재사용 가능한 하단 네비게이션 바입니다.
 * currentRoute를 통해 현재 선택된 탭을 표시합니다.
 *
 * @param currentRoute 현재 선택된 화면의 route (예: "home", "mylibrary", "community", "mypage")
 * @param onHomeClick 홈 버튼 클릭 콜백
 * @param onMyLibraryClick 내 서재 버튼 클릭 콜백
 * @param onCommunityClick 커뮤니티 버튼 클릭 콜백
 * @param onMyPageClick 마이페이지 버튼 클릭 콜백
 */
@Composable
fun ReadPickBottomNavigation(
    currentRoute: String,
    onHomeClick: () -> Unit,
    onMyLibraryClick: () -> Unit,
    onCommunityClick: () -> Unit,
    onMyPageClick: () -> Unit
) {
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
            BottomNavItem(
                label = "홈",
                painter = painterResource(id = R.drawable.ic_home),
                isSelected = currentRoute == "home",
                onClick = onHomeClick
            )
            BottomNavItem(
                label = "내 서재",
                painter = painterResource(id = R.drawable.ic_library),
                isSelected = currentRoute == "mylibrary",
                onClick = onMyLibraryClick
            )
            BottomNavItem(
                label = "커뮤니티",
                painter = painterResource(id = R.drawable.ic_community),
                isSelected = currentRoute == "community",
                onClick = onCommunityClick
            )
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
 * 하단 네비게이션 개별 아이템
 *
 * @param label 하단에 표시될 텍스트
 * @param painter 아이콘 리소스 (Painter)
 * @param isSelected 현재 선택된 상태 여부
 * @param onClick 클릭 시 콜백
 */
@Composable
private fun BottomNavItem(
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