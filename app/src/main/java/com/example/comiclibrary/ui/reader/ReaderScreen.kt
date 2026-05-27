package com.example.comiclibrary.ui.reader

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Toc
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size
import com.example.comiclibrary.ui.component.ErrorStateView
import com.example.comiclibrary.ui.component.LoadingIndicator
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    onBack: () -> Unit,
    viewModel: ReaderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) { LoadingIndicator(); return }
    if (uiState.error != null) {
        ErrorStateView(message = uiState.error!!, onRetry = {})
        return
    }
    val comic = uiState.comic ?: return
    val totalPages = uiState.imageUris.size

    val listState = rememberLazyListState()
    val pagerState = rememberPagerState(
        initialPage = uiState.currentPage.coerceIn(0, totalPages.coerceAtLeast(1) - 1),
        pageCount = { totalPages.coerceAtLeast(1) }
    )
    var seekTrigger by remember { mutableIntStateOf(-1) }

    // Sync current page FROM scroll/pager TO viewModel — keyed by readingMode to
    // prevent stale LaunchedEffect from overriding the page after a mode switch.
    if (uiState.readingMode == ReaderMode.SCROLL) {
        val visiblePage by remember { derivedStateOf { listState.firstVisibleItemIndex + 1 } }
        LaunchedEffect(visiblePage, uiState.readingMode) {
            if (uiState.readingMode == ReaderMode.SCROLL) {
                viewModel.setCurrentPage(visiblePage - 1)
            }
        }
    } else {
        LaunchedEffect(pagerState.currentPage, uiState.readingMode) {
            if (uiState.readingMode == ReaderMode.PAGE) {
                viewModel.setCurrentPage(pagerState.currentPage)
            }
        }
    }

    // Sync seek FROM slider TO scroll/pager
    LaunchedEffect(seekTrigger) {
        if (seekTrigger >= 0 && seekTrigger < totalPages) {
            if (uiState.readingMode == ReaderMode.SCROLL) {
                listState.scrollToItem(seekTrigger)
            } else {
                pagerState.scrollToPage(seekTrigger)
            }
        }
    }

    // When switching reading mode, save target page first, seek after widget is ready
    LaunchedEffect(uiState.readingMode) {
        val target = uiState.currentPage.coerceIn(0, totalPages.coerceAtLeast(1) - 1)
        // Small delay to let the new widget (pager/list) settle before seeking
        delay(50)
        seekTrigger = target
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Layer 1: Content area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { viewModel.toggleOverlay() }
                }
        ) {
            when (uiState.readingMode) {
                ReaderMode.SCROLL -> {
                    LazyColumn(state = listState) {
                        itemsIndexed(uiState.imageUris, key = { _, uri -> uri }) { index, uriStr ->
                            AsyncImage(
                                model = ImageRequest.Builder(LocalPlatformContext.current)
                                    .data(Uri.parse(uriStr))
                                    .crossfade(true)
                                    .size(Size.ORIGINAL)
                                    .build(),
                                contentDescription = "第 ${index + 1} 页",
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.FillWidth
                            )
                        }
                    }
                }
                ReaderMode.PAGE -> {
                    HorizontalPager(state = pagerState, beyondViewportPageCount = 3) { page ->
                        val uriStr = uiState.imageUris.getOrNull(page)
                        if (uriStr != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalPlatformContext.current)
                                    .data(Uri.parse(uriStr))
                                    .crossfade(true)
                                    .size(Size.ORIGINAL)
                                    .build(),
                                contentDescription = "第 ${page + 1} 页",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            }
        }

        // Layer 2: Overlay
        AnimatedVisibility(
            visible = uiState.showOverlay,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            ) {
                TopAppBar(
                    title = { Text(comic.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Black.copy(alpha = 0.85f),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                var sliderPage by remember { mutableIntStateOf(uiState.currentPage) }
                var isDragging by remember { mutableStateOf(false) }

                // Sync slider from page changes when not dragging
                LaunchedEffect(uiState.currentPage) {
                    if (!isDragging) {
                        sliderPage = uiState.currentPage
                    }
                }

                BottomAppBar(
                    containerColor = Color.Black.copy(alpha = 0.85f),
                    contentColor = Color.White,
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                        if (totalPages > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${sliderPage + 1}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Slider(
                                    value = sliderPage.toFloat(),
                                    onValueChange = {
                                        sliderPage = it.toInt()
                                        isDragging = true
                                        seekTrigger = sliderPage
                                    },
                                    onValueChangeFinished = {
                                        isDragging = false
                                    },
                                    valueRange = 0f..((totalPages - 1).coerceAtLeast(0)).toFloat(),
                                    modifier = Modifier.weight(1f).height(20.dp),
                                    colors = SliderDefaults.colors(
                                        thumbColor = Color.White,
                                        activeTrackColor = Color.White,
                                        inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                                    )
                                )
                                Text(
                                    text = "$totalPages",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (totalPages > 0) "${uiState.currentPage + 1} / $totalPages 页" else "无页面",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { viewModel.toggleReadingMode() }) {
                                Icon(
                                    imageVector = if (uiState.readingMode == ReaderMode.SCROLL)
                                        Icons.AutoMirrored.Filled.MenuBook else Icons.AutoMirrored.Filled.Toc,
                                    contentDescription = "切换阅读模式",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
