package com.example.comiclibrary.ui.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.comiclibrary.ui.component.ComicCoverCard
import com.example.comiclibrary.ui.component.EmptyStateView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionDetailScreen(
    onComicClick: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: CollectionDetailViewModel = hiltViewModel()
) {
    val collection by viewModel.collection.collectAsStateWithLifecycle()
    val comics by viewModel.comics.collectAsStateWithLifecycle()
    val gridColumns by viewModel.gridColumns.collectAsStateWithLifecycle()
    var showEditDialog by remember { mutableStateOf(false) }
    val columnWidth = remember(gridColumns) {
        when (gridColumns) { 3 -> 160.dp; 4 -> 130.dp; 5 -> 100.dp; else -> 150.dp }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(collection?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.cycleGridColumns() }) {
                        Icon(Icons.Outlined.GridView, contentDescription = "网格密度")
                    }
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Outlined.Edit, contentDescription = "编辑收藏夹")
                    }
                }
            )
        }
    ) { innerPadding ->
        val desc = collection?.description
        if (comics.isEmpty() && desc.isNullOrBlank()) {
            EmptyStateView(
                icon = Icons.Outlined.FolderOff,
                title = "收藏夹为空",
                subtitle = "去漫画详情页面将漫画添加到此收藏夹",
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                val availableWidth = maxWidth
                val density = androidx.compose.ui.platform.LocalDensity.current.density
                val columns = remember(availableWidth, columnWidth, density) {
                    val colWidthPx = columnWidth.value * density
                    val raw = (availableWidth.value * density / colWidthPx).toInt()
                    raw.coerceIn(1, 8)
                }
                val horizontalPadding = 12.dp
                val totalSpacing = 10.dp * (columns - 1).coerceAtLeast(0).toFloat()
                val itemWidth = (availableWidth - horizontalPadding - horizontalPadding - totalSpacing) / columns.toFloat()

                val rows = remember(comics, columns) {
                    comics.chunked(columns)
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = horizontalPadding,
                        end = horizontalPadding,
                        top = 8.dp,
                        bottom = 12.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!desc.isNullOrBlank()) {
                        item(key = "description") {
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }

                    items(rows, key = { "row_${it.firstOrNull()?.id ?: rows.indexOf(it)}" }) { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            row.forEach { comic ->
                                ComicCoverCard(
                                    comic = comic,
                                    onClick = { onComicClick(comic.id) },
                                    modifier = Modifier.width(itemWidth),
                                    onFavoriteClick = { viewModel.toggleFavorite(comic.id, comic.isFavorite) }
                                )
                            }
                            repeat(columns - row.size) {
                                Spacer(Modifier.width(itemWidth))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        var editedName by remember(collection) { mutableStateOf(collection?.name ?: "") }
        var editedDesc by remember(collection) { mutableStateOf(collection?.description ?: "") }
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("编辑收藏夹") },
            text = {
                androidx.compose.foundation.layout.Column {
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
                    onClick = {
                        viewModel.updateCollection(editedName, editedDesc)
                        showEditDialog = false
                    },
                    enabled = editedName.isNotBlank()
                ) { Text("确认") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("取消") }
            }
        )
    }
}
