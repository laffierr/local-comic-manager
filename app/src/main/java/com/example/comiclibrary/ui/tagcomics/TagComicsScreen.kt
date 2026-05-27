package com.example.comiclibrary.ui.tagcomics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.comiclibrary.ui.component.ComicCoverCard
import com.example.comiclibrary.ui.component.EmptyStateView

private data class SortOption(val label: String, val mode: TagComicsSortMode)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagComicsScreen(
    onBack: () -> Unit,
    onComicClick: (Long) -> Unit,
    viewModel: TagComicsViewModel = hiltViewModel()
) {
    val tagName by viewModel.tagName.collectAsStateWithLifecycle()
    val comics by viewModel.sortedComics.collectAsStateWithLifecycle()
    val gridColumns by viewModel.gridColumns.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var sortMode by remember { mutableStateOf(TagComicsSortMode.DATE_ADDED) }

    val columns = 4
    val rowsPerPage = 5
    val itemsPerPage = columns * rowsPerPage
    val scope = rememberCoroutineScope()

    val pages = remember(comics) { comics.chunked(itemsPerPage).ifEmpty { listOf(emptyList()) } }
    val pageCount = pages.size
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { pageCount })

    var jumpPageText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

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
                        IconButton(onClick = { viewModel.batchFavorite() }) {
                            Icon(Icons.Filled.Favorite, contentDescription = "收藏")
                        }
                        IconButton(onClick = { viewModel.batchDelete() }) {
                            Icon(Icons.Filled.Delete, contentDescription = "删除")
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = {
                        Text(text = tagName, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        if (comics.isEmpty()) {
            EmptyStateView(
                icon = Icons.AutoMirrored.Outlined.LibraryBooks,
                title = "暂无漫画",
                subtitle = "该分类下还没有漫画",
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Sort chips (hidden in selection mode)
                if (!uiState.selectionMode) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val options = listOf(
                            SortOption("最近添加", TagComicsSortMode.DATE_ADDED),
                            SortOption("按标题", TagComicsSortMode.TITLE),
                            SortOption("按页数", TagComicsSortMode.PAGE_COUNT)
                        )
                        options.forEach { opt ->
                            FilterChip(
                                selected = sortMode == opt.mode,
                                onClick = {
                                    sortMode = opt.mode
                                    viewModel.setSortMode(opt.mode)
                                },
                                label = { Text(opt.label) }
                            )
                        }
                    }
                }

                // Paginated grid
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { page ->
                    val pageComics = pages.getOrElse(page) { emptyList() }
                    val rows = pageComics.chunked(columns)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        rows.forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                row.forEach { comic ->
                                    ComicCoverCard(
                                        comic = comic,
                                        onClick = {
                                            if (uiState.selectionMode) {
                                                viewModel.onToggleSelection(comic.id)
                                            } else {
                                                onComicClick(comic.id)
                                            }
                                        },
                                        onLongClick = {
                                            viewModel.onLongPress(comic.id)
                                        },
                                        modifier = Modifier.weight(1f),
                                        selectionMode = uiState.selectionMode,
                                        isSelected = uiState.selectedIds.contains(comic.id),
                                        onFavoriteClick = if (!uiState.selectionMode) {
                                            { viewModel.toggleFavorite(comic.id, comic.isFavorite) }
                                        } else null
                                    )
                                }
                                repeat(columns - row.size) {
                                    Spacer(Modifier.weight(1f))
                                }
                            }
                        }
                        repeat(rowsPerPage - rows.size) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }

                // Bottom bar (hidden in selection mode)
                if (!uiState.selectionMode) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (pagerState.currentPage > 0) {
                                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                                }
                            },
                            enabled = pagerState.currentPage > 0
                        ) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "上一页")
                        }

                        Text(
                            text = "${pagerState.currentPage + 1}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = " / $pageCount",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(end = 12.dp)
                        )

                        OutlinedTextField(
                            value = jumpPageText,
                            onValueChange = { newVal ->
                                if (newVal.isEmpty() || newVal.all { it.isDigit() }) {
                                    jumpPageText = newVal
                                }
                            },
                            modifier = Modifier.width(72.dp).height(48.dp),
                            placeholder = { Text("页", style = MaterialTheme.typography.bodySmall) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Go
                            ),
                            keyboardActions = KeyboardActions(
                                onGo = {
                                    val target = jumpPageText.toIntOrNull()?.minus(1)
                                        ?.coerceIn(0, pageCount - 1)
                                    if (target != null) {
                                        scope.launch { pagerState.animateScrollToPage(target) }
                                        jumpPageText = ""
                                        focusManager.clearFocus()
                                    }
                                }
                            ),
                            textStyle = MaterialTheme.typography.bodySmall
                        )

                        TextButton(
                            onClick = {
                                val target = jumpPageText.toIntOrNull()?.minus(1)
                                    ?.coerceIn(0, pageCount - 1)
                                if (target != null) {
                                    scope.launch { pagerState.animateScrollToPage(target) }
                                    jumpPageText = ""
                                    focusManager.clearFocus()
                                }
                            },
                            enabled = jumpPageText.isNotEmpty()
                        ) { Text("跳转") }

                        IconButton(
                            onClick = {
                                if (pagerState.currentPage < pageCount - 1) {
                                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                                }
                            },
                            enabled = pagerState.currentPage < pageCount - 1
                        ) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "下一页")
                        }
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    uiState.deleteConfirmComicId?.let {
        AlertDialog(
            onDismissRequest = { viewModel.cancelDelete() },
            title = { Text("删除漫画") },
            text = { Text("确定要删除这本漫画吗？") },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmDelete() }) { Text("确认") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cancelDelete() }) { Text("取消") }
            }
        )
    }
}
