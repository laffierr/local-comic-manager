package com.example.comiclibrary.ui.bookshelf

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.comiclibrary.domain.model.Comic
import com.example.comiclibrary.ui.component.ComicCoverCard
import com.example.comiclibrary.ui.component.ConfirmDeleteDialog
import com.example.comiclibrary.ui.component.EmptyStateView

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun BookshelfScreen(
    onComicClick: (Long) -> Unit,
    onNavigateToCollection: (Long) -> Unit = {},
    onNavigateToTagComics: (tagId: Long?, tagName: String) -> Unit = { _, _ -> },
    onThemeSettings: () -> Unit = {},
    viewModel: BookshelfViewModel = hiltViewModel()
) {
    val comics by viewModel.comics.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val allTags by viewModel.allTags.collectAsStateWithLifecycle()
    val allCollections by viewModel.allCollections.collectAsStateWithLifecycle()
    val tagRows by viewModel.comicsForTagRows.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val folderPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { treeUri: Uri? ->
        treeUri?.let { uri ->
            context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            viewModel.onFolderSelected(uri)
        }
    }

    val batchFolderPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { treeUri: Uri? ->
        treeUri?.let { uri ->
            context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            viewModel.onBatchImportFolderSelected(uri)
        }
    }

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
                        IconButton(onClick = { viewModel.batchFavorite() }) {
                            Icon(Icons.Filled.Favorite, contentDescription = "收藏")
                        }
                        IconButton(onClick = { viewModel.showBatchTagDialog() }) {
                            Icon(Icons.Outlined.Sell, contentDescription = "打标签")
                        }
                        IconButton(onClick = { viewModel.showBatchCollectionDialog() }) {
                            Icon(Icons.Filled.CreateNewFolder, contentDescription = "添加到收藏夹")
                        }
                        IconButton(onClick = { viewModel.batchDelete() }) {
                            Icon(Icons.Filled.Delete, contentDescription = "删除")
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = { Text("书架") },
                    actions = {
                        IconButton(onClick = { batchFolderPicker.launch(null) }) {
                            Icon(Icons.Filled.CreateNewFolder, contentDescription = "批量导入")
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
                FloatingActionButton(onClick = { folderPicker.launch(null) }) {
                    Icon(Icons.Default.Add, contentDescription = "添加漫画")
                }
            }
        }
    ) { innerPadding ->
        if (comics.isEmpty() && uiState.searchQuery.isBlank()) {
            EmptyStateView(
                icon = Icons.AutoMirrored.Outlined.LibraryBooks,
                title = "书架空空",
                subtitle = "点击右下角的 + 按钮，选择一个漫画文件夹来添加",
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // --- Header: Search with tag dropdown, Tag Rows ---
                if (!uiState.selectionMode) {
                    // Search bar with expandable tag filter
                    item(key = "search") {
                        var showTagFilter by remember { mutableStateOf(false) }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            OutlinedTextField(
                                value = uiState.searchQuery,
                                onValueChange = viewModel::onSearchQueryChange,
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("搜索漫画…") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                trailingIcon = {
                                    IconButton(onClick = { showTagFilter = !showTagFilter }) {
                                        Icon(
                                            Icons.Outlined.Sell,
                                            contentDescription = "标签筛选",
                                            tint = if (uiState.selectedTagId != null || showTagFilter)
                                                MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                singleLine = true
                            )
                            AnimatedVisibility(
                                visible = showTagFilter,
                                enter = expandVertically(),
                                exit = shrinkVertically()
                            ) {
                                Column {
                                    Spacer(Modifier.height(6.dp))
                                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        FilterChip(
                                            selected = uiState.selectedTagId == null,
                                            onClick = { viewModel.selectTag(null, null) },
                                            label = { Text("全部") }
                                        )
                                        allTags.forEach { tag ->
                                            FilterChip(
                                                selected = uiState.selectedTagId == tag.id,
                                                onClick = { viewModel.selectTag(tag.id, tag.name) },
                                                label = { Text(tag.name) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Horizontal tag rows (only when no tag filter active)
                    if (uiState.selectedTagId == null) {
                        if (comics.isNotEmpty()) {
                            item(key = "all_header") {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 4.dp),
                                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "全部",
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier.weight(1f)
                                    )
                                    TextButton(onClick = {
                                        onNavigateToTagComics(null, "全部漫画")
                                    }) {
                                        Text("全部")
                                    }
                                }
                            }
                            item(key = "all_row") {
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth().height(200.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp)
                                ) {
                                    items(comics.take(10), key = { it.id }) { comic ->
                                        ComicCoverCard(
                                            comic = comic,
                                            onClick = { onComicClick(comic.id) },
                                            modifier = Modifier.width(120.dp),
                                            onFavoriteClick = { viewModel.toggleFavorite(comic.id, comic.isFavorite) }
                                        )
                                    }
                                }
                            }
                        }

                        tagRows.forEach { (tag, tagComics) ->
                            if (tagComics.isNotEmpty()) {
                                item(key = "tag_header_${tag.id}") {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 4.dp),
                                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = tag.name,
                                            style = MaterialTheme.typography.titleSmall,
                                            modifier = Modifier.weight(1f)
                                        )
                                        TextButton(onClick = {
                                            onNavigateToTagComics(tag.id, tag.name)
                                        }) {
                                            Text("全部")
                                        }
                                    }
                                }
                                item(key = "tag_row_${tag.id}") {
                                    LazyRow(
                                        modifier = Modifier.fillMaxWidth().height(200.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp)
                                    ) {
                                        items(tagComics, key = { it.id }) { comic ->
                                            ComicCoverCard(
                                                comic = comic,
                                                onClick = { onComicClick(comic.id) },
                                                modifier = Modifier.width(120.dp),
                                                onFavoriteClick = { viewModel.toggleFavorite(comic.id, comic.isFavorite) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add comic dialog
    if (uiState.showTitleDialog) {
        var editedTitle by remember(uiState.suggestedTitle) { mutableStateOf(uiState.suggestedTitle) }
        AlertDialog(
            onDismissRequest = { viewModel.cancelAddComic() },
            title = { Text("添加漫画") },
            text = {
                OutlinedTextField(
                    value = editedTitle,
                    onValueChange = { editedTitle = it },
                    label = { Text("漫画标题") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmAddComic(editedTitle) }, enabled = !uiState.isAdding) {
                    Text("确认")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cancelAddComic() }) { Text("取消") }
            }
        )
    }

    // Delete confirm
    uiState.deleteConfirmComicId?.let {
        ConfirmDeleteDialog(
            title = "删除漫画",
            message = "确定要删除这本漫画吗？",
            onConfirm = { viewModel.confirmDelete() },
            onDismiss = { viewModel.cancelDelete() }
        )
    }

    // Batch tag dialog
    if (uiState.showBatchTagDialog) {
        var newTagName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { viewModel.cancelBatchTagDialog() },
            title = { Text("批量添加标签") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newTagName,
                        onValueChange = { newTagName = it },
                        label = { Text("新建标签名") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    allTags.forEach { tag ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(onClick = { viewModel.batchAddTag(tag.id) })
                                .padding(8.dp)
                        ) {
                            Text(tag.name)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.batchCreateAndAddTag(newTagName) },
                    enabled = newTagName.isNotBlank()
                ) { Text("新建并添加") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cancelBatchTagDialog() }) { Text("取消") }
            }
        )
    }

    // Batch collection dialog
    if (uiState.showBatchCollectionDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.cancelBatchCollectionDialog() },
            title = { Text("添加到收藏夹") },
            text = {
                LazyColumn {
                    items(allCollections) { col ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(onClick = { viewModel.batchAddToCollection(col.id) })
                                .padding(8.dp)
                        ) {
                            Text(col.name)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { viewModel.cancelBatchCollectionDialog() }) { Text("取消") }
            }
        )
    }

    // Batch import progress dialog
    if (uiState.batchImportInProgress) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("正在批量导入") },
            text = {
                Column {
                    Text(uiState.batchImportProgressText)
                    Spacer(Modifier.height(12.dp))
                    if (uiState.batchImportTotal > 0) {
                        LinearProgressIndicator(
                            progress = {
                                uiState.batchImportCurrent.toFloat() / uiState.batchImportTotal.toFloat()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "${uiState.batchImportCurrent} / ${uiState.batchImportTotal}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        CircularProgressIndicator()
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { viewModel.cancelBatchImport() }) { Text("取消") }
            }
        )
    }

    // Batch import result dialog
    uiState.batchImportResult?.let { result ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissBatchImportResult() },
            title = { Text("导入完成") },
            text = {
                Text(
                    if (result.importedCount > 0) {
                        "已导入 ${result.importedCount} 本漫画到收藏夹「${result.collectionName}」\n跳过 ${result.skippedCount} 项"
                    } else {
                        "没有导入任何漫画\n跳过 ${result.skippedCount} 项"
                    }
                )
            },
            confirmButton = {
                if (result.importedCount > 0) {
                    TextButton(onClick = {
                        viewModel.dismissBatchImportResult()
                        onNavigateToCollection(result.collectionId)
                    }) { Text("查看收藏夹") }
                } else {
                    TextButton(onClick = { viewModel.dismissBatchImportResult() }) { Text("关闭") }
                }
            },
            dismissButton = {
                if (result.importedCount > 0) {
                    TextButton(onClick = { viewModel.dismissBatchImportResult() }) { Text("关闭") }
                }
            }
        )
    }

    // Error dialog
    uiState.error?.let { msg ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissError() },
            title = { Text("提示") },
            text = { Text(msg) },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissError() }) { Text("确定") }
            }
        )
    }
}
