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

/** 预计算固定属性，避免每项重复判断 */
@Immutable
data class CollectionItem(
    val id: Long,
    val name: String,
    val description: String,
    val isPinned: Boolean,
    val isFavorites: Boolean
)
