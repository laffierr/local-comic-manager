package com.example.comiclibrary.ui.reader

import androidx.compose.runtime.Immutable
import com.example.comiclibrary.domain.model.Comic

enum class ReaderMode { SCROLL, PAGE }

@Immutable
data class ReaderUiState(
    val comic: Comic? = null,
    val imageUris: List<String> = emptyList(),
    val currentPage: Int = 0,
    val readingMode: ReaderMode = ReaderMode.SCROLL,
    val showOverlay: Boolean = true,
    val isLoading: Boolean = true,
    val error: String? = null
)
