package com.nextread.readpick.presentation.collection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nextread.readpick.ui.theme.NextReadTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionCreateScreen(
    onDismiss: () -> Unit,
    onNext: (collectionName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var collectionName by remember { mutableStateOf("") }
    val isNameValid = collectionName.length in 2..20

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("책장 추가", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "닫기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // 01. 책장 이름 입력 안내
            Text(
                text = "01. 책장명을 입력하세요",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "책장명은 2자 이상 20자 이하로 입력 가능해요.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 책장 이름 입력 필드
            OutlinedTextField(
                value = collectionName,
                onValueChange = { collectionName = it.take(20) },
                label = { Text("책장명") },
                singleLine = true,
                isError = collectionName.isNotEmpty() && !isNameValid,
                supportingText = {
                    if (collectionName.isNotEmpty() && !isNameValid) {
                        Text("책장명은 2자 이상 20자 이하로 입력해야 합니다.")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 02. 책 추가 안내
            Text(
                text = "02. 책장에 추가할 도서를 선택하세요.",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 키보드 영역을 대체할 공백
            Spacer(modifier = Modifier.weight(1f))

            // 다음 버튼 (키보드 위 영역을 대체)
            Button(
                onClick = { onNext(collectionName) },
                enabled = isNameValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("다음")
            }
        }
    }
}

// 🚨🚨🚨 주석을 제거하고 아래 코드를 복구합니다. 🚨🚨🚨
@Preview(showBackground = true)
@Composable
fun CollectionCreateScreenPreview() {
    NextReadTheme {
        CollectionCreateScreen(onDismiss = {}, onNext = {})
    }
}