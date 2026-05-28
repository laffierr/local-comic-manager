package com.example.comiclibrary.ui.favorites

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.comiclibrary.ui.component.ConfirmDeleteDialog
import com.example.comiclibrary.ui.component.EmptyStateView

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FavoritesScreen(
    onCollectionClick: (Long) -> Unit,
    onNavigateToTags: () -> Unit = {},
    onThemeSettings: () -> Unit = {},
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val collections by viewModel.collections.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissSnackbar()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    title = { Text("收藏夹") },
                    actions = {
                        IconButton(onClick = onNavigateToTags) {
                            Icon(Icons.Outlined.Sell, contentDescription = "标签管理")
                        }
                        IconButton(onClick = onThemeSettings) {
                            Icon(Icons.Filled.Settings, contentDescription = "主题设置")
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (!uiState.selectionMode) {
                FloatingActionButton(onClick = { viewModel.showCreateDialog() }) {
                    Icon(Icons.Default.Add, contentDescription = "新建收藏夹")
                }
            }
        }
    ) { innerPadding ->
        if (collections.isEmpty()) {
            EmptyStateView(
                icon = Icons.Filled.FavoriteBorder,
                title = "还没有收藏夹",
                subtitle = "点击右下角的 + 按钮来创建一个收藏夹",
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            // 当前打开菜单的收藏夹数据（用于顶层的共享 DropdownMenu）
            val menuItem = uiState.menuCollectionId?.let { mid ->
                collections.find { it.id == mid }
            }

            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(
                        items = collections,
                        key = { it.id }
                    ) { item ->
                        ListItem(
                            headlineContent = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(item.name)
                                    if (item.isPinned) {
                                        Icon(
                                            Icons.Filled.PushPin,
                                            contentDescription = "已置顶",
                                            modifier = Modifier.padding(start = 4.dp).size(16.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            },
                            supportingContent = {
                                if (item.description.isNotBlank()) {
                                    Text(text = item.description, maxLines = 2)
                                }
                            },
                            leadingContent = {
                                if (uiState.selectionMode && !item.isFavorites) {
                                    Checkbox(
                                        checked = item.id in uiState.selectedIds,
                                        onCheckedChange = { viewModel.onToggleSelection(item.id) }
                                    )
                                }
                            },
                            trailingContent = {
                                if (!uiState.selectionMode) {
                                    Row {
                                        IconButton(onClick = { viewModel.startEdit(item.id) }) {
                                            Icon(Icons.Outlined.Edit, contentDescription = "编辑")
                                        }
                                        IconButton(onClick = { viewModel.showMenu(item.id) }) {
                                            Icon(Icons.Filled.MoreVert, contentDescription = "更多")
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.combinedClickable(
                                onClick = {
                                    if (uiState.selectionMode) viewModel.onToggleSelection(item.id)
                                    else onCollectionClick(item.id)
                                },
                                onLongClick = {
                                    if (!uiState.selectionMode) viewModel.onLongPress(item.id)
                                }
                            )
                        )
                    }
                }

                // 共享 DropdownMenu，只渲染一次，不在每个 item 里重复
                if (menuItem != null) {
                    DropdownMenu(
                        expanded = true,
                        onDismissRequest = { viewModel.dismissMenu() }
                    ) {
                        DropdownMenuItem(
                            text = { Text("编辑") },
                            onClick = {
                                viewModel.dismissMenu()
                                viewModel.startEdit(menuItem.id)
                            },
                            leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text(if (menuItem.isPinned) "取消置顶" else "置顶") },
                            onClick = { viewModel.togglePin(menuItem.id) },
                            leadingIcon = {
                                Icon(
                                    if (menuItem.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                    contentDescription = null
                                )
                            }
                        )
                        if (!menuItem.isFavorites) {
                            DropdownMenuItem(
                                text = { Text("删除") },
                                onClick = {
                                    viewModel.dismissMenu()
                                    viewModel.requestDelete(menuItem.id)
                                },
                                leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null) }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("多选") },
                            onClick = {
                                viewModel.dismissMenu()
                                viewModel.enterSelectionMode(menuItem.id)
                            },
                            leadingIcon = { Icon(Icons.Outlined.CheckCircle, contentDescription = null) }
                        )
                    }
                }
            }
        }
    }

    // Create collection dialog
    if (uiState.showCreateDialog) {
        var name by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { viewModel.cancelCreateDialog() },
            title = { Text("新建收藏夹") },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("收藏夹名称") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("描述（可选）") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.createCollection(name, description) },
                    enabled = name.isNotBlank()
                ) { Text("确认") }
            },
            dismissButton = { TextButton(onClick = { viewModel.cancelCreateDialog() }) { Text("取消") } }
        )
    }

    // Edit collection dialog
    uiState.editingCollectionId?.let { editId ->
        val target = collections.find { it.id == editId }
        if (target != null) {
            var editedName by remember(target) { mutableStateOf(target.name) }
            var editedDesc by remember(target) { mutableStateOf(target.description) }
            AlertDialog(
                onDismissRequest = { viewModel.cancelEdit() },
                title = { Text("编辑收藏夹") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = editedName,
                            onValueChange = { editedName = it },
                            label = { Text("名称") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = editedDesc,
                            onValueChange = { editedDesc = it },
                            label = { Text("简介") },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.updateCollection(editId, editedName, editedDesc) },
                        enabled = editedName.isNotBlank()
                    ) { Text("确认") }
                },
                dismissButton = { TextButton(onClick = { viewModel.cancelEdit() }) { Text("取消") } }
            )
        }
    }

    // Delete confirm
    uiState.deleteConfirmCollectionId?.let {
        ConfirmDeleteDialog(
            title = "删除收藏夹",
            message = "确定要删除这个收藏夹吗？\n漫画本身不会被删除。",
            onConfirm = { viewModel.confirmDelete() },
            onDismiss = { viewModel.cancelDelete() }
        )
    }
}
