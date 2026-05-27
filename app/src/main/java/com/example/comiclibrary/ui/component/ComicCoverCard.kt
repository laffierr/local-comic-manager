package com.example.comiclibrary.ui.component

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.comiclibrary.domain.model.Comic

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ComicCoverCard(
    comic: Comic,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    selectionMode: Boolean = false,
    isSelected: Boolean = false,
    onFavoriteClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick
        ),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            else MaterialTheme.colorScheme.surfaceContainer
        ),
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Box {
            AsyncImage(
                model = comic.coverUri?.let { Uri.parse(it) },
                contentDescription = comic.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.72f)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            if (selectionMode) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.72f)
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clip(RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Icon(
                        imageVector = if (isSelected) Icons.Filled.CheckCircle
                        else Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.padding(8.dp).size(28.dp),
                        tint = if (isSelected) MaterialTheme.colorScheme.primary
                        else Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            if (!selectionMode && onFavoriteClick != null) {
                Icon(
                    imageVector = if (comic.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (comic.isFavorite) "取消收藏" else "收藏",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(24.dp)
                        .clickable { onFavoriteClick?.invoke() },
                    tint = if (comic.isFavorite) Color(0xFFFF4081) else Color.White.copy(alpha = 0.85f)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.72f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent, Color.Transparent,
                                Color.Black.copy(alpha = 0.75f)
                            )
                        )
                    ),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = comic.title,
                    modifier = Modifier.padding(8.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
            }
        }
    }
}
