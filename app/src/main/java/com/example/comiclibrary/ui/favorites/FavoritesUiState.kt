package com.example.comiclibrary.ui.favorites

import androidx.compose.runtime.Immutable

@Immutable
data class FavoritesUiState(
    val showCreateDialog: Boolean = false,
    val deleteConfirmCollectionId: Long? = null,
    val selectionMode: Boolean = false,
    val selectedIds: Set<Long> = emptySet(),
    val editingCollectionId: Long? = null,
    val menuCollectionId: Long? = null,
    val snackbarMessage: String? = null
)
