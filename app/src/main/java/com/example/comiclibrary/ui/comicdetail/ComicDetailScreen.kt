package com.example.comiclibrary.ui.comicdetail

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.comiclibrary.ui.component.ConfirmDeleteDialog
import com.example.comiclibrary.ui.component.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ComicDetailScreen(
    onBack: () -> Unit,
    onDeleted: () -> Unit,
    onStartReading: (Long) -> Unit,
    onNavigateToTags: () -> Unit = {},
    viewModel: ComicDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showOverflowMenu by remember { mutableStateOf(false) }

    if (uiState.isLoading) { LoadingIndicator(); return }
    val comic = uiState.comic ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(comic.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            imageVector = if (comic.isFavorite) Icons.Filled.Favorite
                            else Icons.Filled.FavoriteBorder,
                            contentDescription = "收藏",
                            tint = if (comic.isFavorite)
                                MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { viewModel.showDeleteDialog() }) {
                        Icon(Icons.Filled.Delete, contentDescription = "删除")
                    }
                    Box {
                        IconButton(onClick = { showOverflowMenu = true }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "更多")
                        }
                        DropdownMenu(
                            expanded = showOverflowMenu,
                            onDismissRequest = { showOverflowMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("管理标签") },
                                onClick = {
                                    showOverflowMenu = false
                                    onNavigateToTags()
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Cover image
            AsyncImage(
                model = comic.coverUri?.let { Uri.parse(it) },
                contentDescription = comic.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.72f)
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    .clickable { onStartReading(comic.id) },
                contentScale = ContentScale.Crop
            )

            // Start reading button
            Button(
                onClick = { onStartReading(comic.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("开始阅读")
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Title and basic info
                Text(comic.title, style = MaterialTheme.typography.headlineSmall)

                Spacer(Modifier.height(6.dp))

                Row {
                    Text(
                        "${comic.pageCount} 页",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        "添加于 ${viewModel.formatDate(comic.addedAtMillis)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Tags section
                Spacer(Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(Modifier.height(12.dp))

                Row {
                    Text("标签", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    IconButton(onClick = { viewModel.showNewTagDialog() }) {
                        Icon(Icons.AutoMirrored.Filled.Label, contentDescription = "添加标签", modifier = Modifier.padding(0.dp))
                    }
                }

                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.allTags.forEach { tag ->
                        val isTagged = tag.id in uiState.memberTagIds
                        FilterChip(
                            selected = isTagged,
                            onClick = { viewModel.toggleTag(tag.id, isTagged) },
                            label = { Text(tag.name) }
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }

    // New tag dialog
    if (uiState.showNewTagDialog) {
        var tagName by remember { mutableStateOf("") }
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { viewModel.cancelNewTagDialog() },
            title = { Text("新建标签") },
            text = {
                androidx.compose.material3.OutlinedTextField(
                    value = tagName,
                    onValueChange = { tagName = it },
                    label = { Text("标签名") },
                    singleLine = true
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = { viewModel.createAndAddTag(tagName) },
                    enabled = tagName.isNotBlank()
                ) { Text("创建") }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { viewModel.cancelNewTagDialog() }) { Text("取消") }
            }
        )
    }

    // Delete confirm
    if (uiState.showDeleteDialog) {
        ConfirmDeleteDialog(
            title = "删除漫画",
            message = "确定要删除「${comic.title}」吗？",
            onConfirm = {
                viewModel.deleteComic()
                onDeleted()
            },
            onDismiss = { viewModel.cancelDelete() }
        )
    }

}
