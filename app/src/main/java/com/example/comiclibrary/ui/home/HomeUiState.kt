package com.example.comiclibrary.ui.home

import androidx.compose.runtime.Immutable
import com.example.comiclibrary.domain.model.Comic

@Immutable
data class HomeUiState(
    val randomComics: List<Comic> = emptyList(),
    val recentComics: List<Comic> = emptyList(),
    val totalCount: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)
