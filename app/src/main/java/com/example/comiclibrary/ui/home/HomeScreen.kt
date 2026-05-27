package com.example.comiclibrary.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.comiclibrary.ui.component.ComicCoverCard
import com.example.comiclibrary.ui.component.EmptyStateView
import com.example.comiclibrary.ui.component.ErrorStateView
import com.example.comiclibrary.ui.component.ShimmerLoadingGrid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onComicClick: (Long) -> Unit,
    onGoToBookshelf: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("漫画书架") })
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> ShimmerLoadingGrid()
            uiState.error != null -> ErrorStateView(message = uiState.error!!)
            uiState.totalCount == 0 -> EmptyStateView(
                icon = Icons.AutoMirrored.Outlined.MenuBook,
                title = "还没有漫画",
                subtitle = "去书架页面添加你的第一本漫画吧",
                actionLabel = "去书架",
                onAction = onGoToBookshelf,
                modifier = Modifier.padding(innerPadding)
            )
            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "随机推荐",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = uiState.randomComics, key = { it.id }) { comic ->
                        ComicCoverCard(
                            comic = comic,
                            onClick = { onComicClick(comic.id) },
                            modifier = Modifier.fillMaxWidth(0.38f),
                            onFavoriteClick = { viewModel.toggleFavorite(comic.id, comic.isFavorite) }
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "共 ${uiState.totalCount} 本漫画",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "最近添加",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = uiState.recentComics, key = { it.id }) { comic ->
                        ComicCoverCard(
                            comic = comic,
                            onClick = { onComicClick(comic.id) },
                            modifier = Modifier.fillMaxWidth(0.38f),
                            onFavoriteClick = { viewModel.toggleFavorite(comic.id, comic.isFavorite) }
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
