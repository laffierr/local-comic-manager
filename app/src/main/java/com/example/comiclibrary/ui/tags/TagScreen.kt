package com.example.comiclibrary.ui.tags

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.comiclibrary.ui.component.ConfirmDeleteDialog
import com.example.comiclibrary.ui.component.EmptyStateView

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TagScreen(
    onBack: () -> Unit,
    viewModel: TagViewModel = hiltViewModel()
) {
    val tags by viewModel.tags.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            if (uiState.selectionMode) {
                TopAppBar(
                    title = { Text("已选 ${uiState.selectedIds.size} 项") },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.exitSelectionMode() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "取消")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.onSelectAll() }) {
                            Text("全选", style = MaterialTheme.typography.labelLarge)
                        }
                        IconButton(onClick = { viewModel.batchDelete() }) {
                            Icon(Icons.Filled.Delete, contentDescription = "删除")
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = { Text("标签管理") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (!uiState.selectionMode) {
                FloatingActionButton(onClick = { viewModel.showCreateDialog() }) {
                    Icon(Icons.Default.Add, contentDescription = "新建标签")
                }
            }
        }
    ) { innerPadding ->
        if (tags.isEmpty()) {
            EmptyStateView(
                icon = Icons.Filled.Sell,
                title = "还没有标签",
                subtitle = "点击右下角的 + 按钮来创建一个标签",
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(items = tags, key = { it.id }) { tag ->
                    ListItem(
                        headlineContent = { Text(tag.name) },
                        leadingContent = {
                            if (uiState.selectionMode) {
                                Checkbox(
                                    checked = tag.id in uiState.selectedIds,
                                    onCheckedChange = { viewModel.onToggleSelection(tag.id) }
                                )
                            }
                        },
                        trailingContent = {
                            if (!uiState.selectionMode) {
                                Row {
                                    IconButton(onClick = { viewModel.startEdit(tag.id) }) {
                                        Icon(Icons.Outlined.Edit, contentDescription = "编辑")
                                    }
                                    IconButton(onClick = { viewModel.requestDelete(tag.id) }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "删除")
                                    }
                                }
                            }
                        },
                        modifier = Modifier.combinedClickable(
                            onClick = {
                                if (uiState.selectionMode) viewModel.onToggleSelection(tag.id)
                            },
                            onLongClick = {
                                if (!uiState.selectionMode) viewModel.onLongPress(tag.id)
                            }
                        )
                    )
                }
            }
        }
    }

    // 新建标签
    if (uiState.showCreateDialog) {
        var name by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { viewModel.cancelCreateDialog() },
            title = { Text("新建标签") },
            text = {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("标签名") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.createTag(name) },
                    enabled = name.isNotBlank()
                ) { Text("确认") }
            },
            dismissButton = { TextButton(onClick = { viewModel.cancelCreateDialog() }) { Text("取消") } }
        )
    }

    // 编辑标签
    uiState.editingTagId?.let { editId ->
        val target = tags.find { it.id == editId }
        if (target != null) {
            var editedName by remember(target) { mutableStateOf(target.name) }
            AlertDialog(
                onDismissRequest = { viewModel.cancelEdit() },
                title = { Text("编辑标签") },
                text = {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("标签名") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.updateTag(editId, editedName) },
                        enabled = editedName.isNotBlank()
                    ) { Text("确认") }
                },
                dismissButton = { TextButton(onClick = { viewModel.cancelEdit() }) { Text("取消") } }
            )
        }
    }

    // 删除确认
    uiState.deleteConfirmTagId?.let { tagId ->
        val tagName = tags.find { it.id == tagId }?.name ?: ""
        ConfirmDeleteDialog(
            title = "删除标签",
            message = "确定要删除标签「$tagName」吗？\n所有漫画上的此标签也将被移除。",
            onConfirm = { viewModel.confirmDelete() },
            onDismiss = { viewModel.cancelDelete() }
        )
    }
}
