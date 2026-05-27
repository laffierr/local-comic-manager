package com.example.comiclibrary.ui.tags

import androidx.compose.runtime.Immutable

@Immutable
data class TagUiState(
    val showCreateDialog: Boolean = false,
    val editingTagId: Long? = null,
    val deleteConfirmTagId: Long? = null,
    val selectionMode: Boolean = false,
    val selectedIds: Set<Long> = emptySet()
)
