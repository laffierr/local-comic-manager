package com.example.comiclibrary.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.comiclibrary.domain.model.Comic

@Composable
fun ComicGrid(
    comics: List<Comic>,
    onComicClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(12.dp),
    columnWidth: Dp = 150.dp,
    selectionMode: Boolean = false,
    selectedIds: Set<Long> = emptySet(),
    onComicLongClick: ((Long) -> Unit)? = null,
    onFavoriteClick: ((Long) -> Unit)? = null
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(columnWidth),
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalItemSpacing = 12.dp
    ) {
        items(
            items = comics,
            key = { it.id }
        ) { comic ->
            ComicCoverCard(
                comic = comic,
                onClick = { onComicClick(comic.id) },
                onLongClick = onComicLongClick?.let { cb -> { cb(comic.id) } },
                selectionMode = selectionMode,
                isSelected = comic.id in selectedIds,
                onFavoriteClick = onFavoriteClick?.let { cb -> { cb(comic.id) } }
            )
        }
    }
}
