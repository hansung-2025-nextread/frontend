package com.nextread.readpick.presentation.collection.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nextread.readpick.domain.model.ReadingStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingStatusDropdown(
    currentStatus: ReadingStatus,
    onStatusChange: (ReadingStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        // 현재 상태 표시 버튼
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = when (currentStatus) {
                    ReadingStatus.NOT_STARTED -> MaterialTheme.colorScheme.surfaceVariant
                    ReadingStatus.READING -> MaterialTheme.colorScheme.primaryContainer
                    ReadingStatus.COMPLETED -> MaterialTheme.colorScheme.tertiaryContainer
                    ReadingStatus.DROPPED -> MaterialTheme.colorScheme.errorContainer
                }
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${currentStatus.emoji} ${currentStatus.displayName}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "상태 변경"
                )
            }
        }

        // 드롭다운 메뉴
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ReadingStatus.values().forEach { status ->
                DropdownMenuItem(
                    text = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = status.emoji)
                            Text(text = status.displayName)

                            // 현재 선택된 상태 표시
                            if (status == currentStatus) {
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "✓",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    },
                    onClick = {
                        onStatusChange(status)
                        expanded = false
                    }
                )
            }
        }
    }
}
