package com.nextread.readpick.presentation.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nextread.readpick.presentation.onboarding.components.CategoryCard

/**
 * 온보딩 화면
 *
 * 신규 사용자가 관심 카테고리를 선택하는 화면입니다.
 * - 8개 카테고리를 2열 그리드로 표시
 * - 사용자가 원하는 만큼 선택 가능
 * - "완료" 버튼 (최소 1개 선택 시 활성화)
 * - "건너뛰기" 버튼 (항상 활성화)
 *
 * @param onOnboardingComplete 온보딩 완료 시 실행할 콜백 (홈으로 이동)
 * @param viewModel OnboardingViewModel (Hilt로 자동 주입)
 */
@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Navigation 이벤트 처리
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is OnboardingViewModel.NavigationEvent.NavigateToHome -> {
                    onOnboardingComplete()
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        when (val state = uiState) {
            is OnboardingUiState.Loading -> {
                LoadingContent(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                )
            }

            is OnboardingUiState.Success -> {
                OnboardingContent(
                    categories = state.categories,
                    selectedCategoryIds = state.selectedCategoryIds,
                    isSubmitting = state.isSubmitting,
                    onCategoryClick = { categoryId ->
                        viewModel.toggleCategorySelection(categoryId)
                    },
                    onCompleteClick = {
                        viewModel.submitSelectedCategories()
                    },
                    onSkipClick = {
                        viewModel.skipOnboarding()
                    },
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                )
            }

            is OnboardingUiState.Error -> {
                ErrorContent(
                    message = state.message,
                    onRetryClick = {
                        viewModel.loadCategories()
                    },
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                )
            }
        }
    }
}

/**
 * 로딩 상태 UI
 */
@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "카테고리를 불러오는 중...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 온보딩 메인 컨텐츠
 */
@Composable
private fun OnboardingContent(
    categories: List<com.nextread.readpick.data.model.onboarding.OnboardingCategoryDto>,
    selectedCategoryIds: Set<Int>,
    isSubmitting: Boolean,
    onCategoryClick: (Int) -> Unit,
    onCompleteClick: () -> Unit,
    onSkipClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 그라데이션 배경
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 진행 인디케이터
            ProgressIndicator(
                selectedCount = selectedCategoryIds.size,
                totalCount = categories.size
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 제목
            Text(
                text = "관심 있는\n카테고리를 선택해주세요",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    lineHeight = 40.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            // 서브 텍스트
            Text(
                text = "선택한 카테고리를 기반으로\nAI가 맞춤 도서를 추천해드려요 📚",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 카테고리 그리드 (2열) - 고정 높이로 스크롤 방지
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                for (i in categories.indices step 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CategoryCard(
                            category = categories[i],
                            isSelected = categories[i].categoryId in selectedCategoryIds,
                            onClick = { onCategoryClick(categories[i].categoryId) },
                            modifier = Modifier.weight(1f)
                        )

                        if (i + 1 < categories.size) {
                            CategoryCard(
                                category = categories[i + 1],
                                isSelected = categories[i + 1].categoryId in selectedCategoryIds,
                                onClick = { onCategoryClick(categories[i + 1].categoryId) },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 하단: 버튼들
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // 완료 버튼
                Button(
                    onClick = onCompleteClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = selectedCategoryIds.isNotEmpty() && !isSubmitting,
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Text(
                        text = if (isSubmitting) "저장 중..." else "시작하기",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                // 건너뛰기 버튼
                TextButton(
                    onClick = onSkipClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = !isSubmitting
                ) {
                    Text(
                        text = "나중에 선택하기",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 하단 여백 추가 (버튼이 잘리지 않도록)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * 진행 인디케이터 컴포넌트
 * X/8 선택됨 표시 및 프로그레스 바
 */
@Composable
private fun ProgressIndicator(
    selectedCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    val progress by animateFloatAsState(
        targetValue = if (totalCount > 0) selectedCount.toFloat() / totalCount else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "progress"
    )

    val progressColor by animateColorAsState(
        targetValue = when {
            selectedCount == 0 -> MaterialTheme.colorScheme.outline
            selectedCount < totalCount / 2 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            else -> MaterialTheme.colorScheme.primary
        },
        label = "progressColor"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        // 선택 개수 표시
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$selectedCount/$totalCount 선택됨",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = if (selectedCount > 0) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )

            // 선택 완료 뱃지
            if (selectedCount > 0) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "✓",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 프로그레스 바
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = progressColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

/**
 * 에러 상태 UI
 */
@Composable
private fun ErrorContent(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "😕",
                style = MaterialTheme.typography.displayLarge
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Button(onClick = onRetryClick) {
                Text(text = "다시 시도")
            }
        }
    }
}
