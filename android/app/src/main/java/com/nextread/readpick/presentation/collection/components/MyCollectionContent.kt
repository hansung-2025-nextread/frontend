package com.nextread.readpick.presentation.collection.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nextread.readpick.ui.theme.NextReadTheme

/**
 * 사용자 컬렉션(책장) DTO
 *
 * 사용자가 만든 커스텀 책장의 정보를 담는 데이터 클래스
 *
 * @param id 컬렉션 고유 ID
 * @param name 컬렉션 이름 (예: "재미있게 읽은 소설", "다음 프로젝트를 위한 자료 모음")
 * @param bookCount 컬렉션에 포함된 책의 개수
 * @param latestCoverUrl 가장 최근에 추가된 책의 표지 URL (썸네일용)
 */
data class UserCollection(
    val id: Long,
    val name: String,
    val bookCount: Int,
    val latestCoverUrl: String? = null
)

/**
 * 내 책장 탭 컨텐츠
 *
 * 사용자가 직접 만든 커스텀 컬렉션 목록을 표시합니다.
 * 컬렉션이 없으면 "내 책장 만들기" 안내 화면을 표시합니다.
 *
 * @param hasCustomCollections 사용자 컬렉션이 존재하는지 여부
 * @param collections 사용자 컬렉션 목록
 * @param onMakeCollectionClick 내 책장 만들기 버튼 클릭 시 호출
 * @param onEditClick 편집 버튼 클릭 시 호출
 * @param onCollectionClick 컬렉션 아이템 클릭 시 호출 (컬렉션 상세로 이동)
 * @param onDeleteCollections 선택된 컬렉션들을 삭제할 때 호출
 * @param onRenameCollection 컬렉션 이름을 변경할 때 호출
 * @param modifier Modifier
 */
@Composable
fun MyCollectionContent(
    hasCustomCollections: Boolean,
    collections: List<UserCollection>,
    onMakeCollectionClick: () -> Unit,
    onEditClick: () -> Unit,
    onCollectionClick: (collectionId: Long) -> Unit,
    onDeleteCollections: (List<Long>) -> Unit = {},
    onRenameCollection: (collectionId: Long, newName: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    if (!hasCustomCollections) {
        // 컬렉션이 없을 때: 안내 화면 표시
        EmptyCollectionState(onMakeCollectionClick)
    } else {
        // 컬렉션이 있을 때: 컬렉션 목록 표시
        CollectionListState(
            collections = collections,
            onEditClick = onEditClick,
            onCollectionClick = onCollectionClick,
            onDeleteCollections = onDeleteCollections,
            onRenameCollection = onRenameCollection,
            onMakeCollectionClick = onMakeCollectionClick
        )
    }
}

/**
 * 컬렉션이 없을 때 표시되는 안내 화면
 *
 * 사용자에게 내 책장을 만들도록 유도하는 화면입니다.
 *
 * @param onMakeCollectionClick 내 책장 만들기 버튼 클릭 시 호출
 */
@Composable
fun EmptyCollectionState(onMakeCollectionClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "내 책장을 만들어 보세요.",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "내 서재의 책들을 원하는 대로 분류할 수 있어요",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onMakeCollectionClick,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("내 책장 만들기", color = Color.White)
            }
        }
    }
}

/**
 * 컬렉션 목록 화면
 *
 * 사용자가 만든 컬렉션(책장) 목록을 표시합니다.
 *
 * @param collections 컬렉션 목록
 * @param onEditClick 편집 버튼 클릭 시 호출
 * @param onCollectionClick 컬렉션 아이템 클릭 시 호출 (컬렉션 ID 전달)
 * @param onDeleteCollections 선택된 컬렉션들을 삭제할 때 호출
 * @param onRenameCollection 컬렉션 이름을 변경할 때 호출
 * @param onMakeCollectionClick 책장 추가 버튼 클릭 시 호출
 */
@Composable
fun CollectionListState(
    collections: List<UserCollection>,
    onEditClick: () -> Unit,
    onCollectionClick: (collectionId: Long) -> Unit,
    onDeleteCollections: (List<Long>) -> Unit = {},
    onRenameCollection: (collectionId: Long, newName: String) -> Unit = { _, _ -> },
    onMakeCollectionClick: () -> Unit = {}
) {

    // 편집 모드 상태
    var isEditMode by remember { mutableStateOf(false) }
    var selectedCollections by remember { mutableStateOf<Set<Long>>(emptySet()) }

    // 이름 변경 다이얼로그 상태
    var showRenameDialog by remember { mutableStateOf(false) }
    var renamingCollection by remember { mutableStateOf<UserCollection?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // 상단 버튼 영역
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isEditMode) {
                Text(
                    text = "${selectedCollections.size}개 선택",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            isEditMode = false
                            selectedCollections = emptySet()
                        },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text("취소", fontSize = 12.sp)
                    }
                    Button(
                        onClick = {
                            onDeleteCollections(selectedCollections.toList())
                            isEditMode = false
                            selectedCollections = emptySet()
                        },
                        enabled = selectedCollections.isNotEmpty(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("삭제", fontSize = 12.sp)
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onMakeCollectionClick,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("책장 추가", fontSize = 12.sp)
                    }
                    Button(
                        onClick = {
                            isEditMode = true
                            onEditClick()
                        },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text("편집", fontSize = 12.sp)
                    }
                }
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(collections) { collection ->
                CollectionListItem(
                    collection = collection,
                    isEditMode = isEditMode,
                    isSelected = selectedCollections.contains(collection.id),
                    onClick = {
                        if (isEditMode) {
                            // 편집 모드: 선택/해제
                            selectedCollections = if (selectedCollections.contains(collection.id)) {
                                selectedCollections - collection.id
                            } else {
                                selectedCollections + collection.id
                            }
                        } else {
                            // 일반 모드: 컬렉션 상세로 이동
                            onCollectionClick(collection.id)
                        }
                    },
                    onRename = {
                        renamingCollection = collection
                        showRenameDialog = true
                    }
                )
            }
            // 하단 네비게이션 공간 확보
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // 이름 변경 다이얼로그
    if (showRenameDialog && renamingCollection != null) {
        RenameCollectionDialog(
            currentName = renamingCollection!!.name,
            onDismiss = { showRenameDialog = false },
            onConfirm = { newName ->
                onRenameCollection(renamingCollection!!.id, newName)
                showRenameDialog = false
            }
        )
    }
}

/**
 * 컬렉션 목록 아이템
 *
 * 개별 컬렉션을 표시하는 리스트 아이템입니다.
 *
 * @param collection 컬렉션 데이터
 * @param isEditMode 편집 모드 여부
 * @param isSelected 선택된 상태 여부 (편집 모드에서만 사용)
 * @param onClick 클릭 시 호출되는 콜백
 * @param onRename 이름 변경 버튼 클릭 시 호출되는 콜백
 */
@Composable
fun CollectionListItem(
    collection: UserCollection,
    isEditMode: Boolean = false,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    onRename: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(
                if (isEditMode && isSelected)
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 편집 모드일 때 체크박스 표시
        if (isEditMode) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onClick() },
                modifier = Modifier.padding(end = 8.dp)
            )
        }

        // 컬렉션 썸네일 (첫 글자 표시)
        // TODO: 컬렉션에 포함된 책 표지 이미지 사용
        Box(
            modifier = Modifier
                .size(60.dp, 90.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = collection.name.first().toString(),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // 컬렉션 정보
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = collection.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${collection.bookCount}권의 책",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 일반 모드일 때만 이름 변경 아이콘과 화살표 표시
        if (!isEditMode) {
            IconButton(onClick = onRename) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "이름 변경",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "컬렉션 상세 보기",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 컬렉션 이름 변경 다이얼로그
 *
 * @param currentName 현재 컬렉션 이름
 * @param onDismiss 다이얼로그 닫기
 * @param onConfirm 확인 버튼 클릭 시 호출 (새 이름 전달)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenameCollectionDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newName by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "책장 이름 변경",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column {
                Text(
                    text = "새로운 책장 이름을 입력하세요",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("책장 이름") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newName.isNotBlank()) {
                        onConfirm(newName.trim())
                    }
                },
                enabled = newName.isNotBlank() && newName.trim() != currentName
            ) {
                Text("변경")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun MyCollectionContentEmptyPreview() {
    NextReadTheme {
        MyCollectionContent(
            hasCustomCollections = false,
            collections = emptyList(),
            onMakeCollectionClick = {},
            onEditClick = {},
            onCollectionClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MyCollectionContentListPreview() {
    val previewCollections = listOf(
        UserCollection(
            id = 1,
            name = "재미있게 읽은 소설",
            bookCount = 5,
            latestCoverUrl = null
        ),
        UserCollection(
            id = 2,
            name = "다음 프로젝트를 위한 자료 모음",
            bookCount = 10,
            latestCoverUrl = null
        )
    )

    NextReadTheme {
        MyCollectionContent(
            hasCustomCollections = true,
            collections = previewCollections,
            onMakeCollectionClick = {},
            onEditClick = {},
            onCollectionClick = {}
        )
    }
}