package com.example.comiclibrary.ui.bookshelf

import android.net.Uri
import androidx.compose.runtime.Immutable

enum class SortMode { DATE_ADDED, TITLE, PAGE_COUNT }

@Immutable
data class BookshelfUiState(
    val isAdding: Boolean = false,
    val pendingFolderUri: Uri? = null,
    val suggestedTitle: String = "",
    val showTitleDialog: Boolean = false,
    val deleteConfirmComicId: Long? = null,
    val searchQuery: String = "",
    val sortMode: SortMode = SortMode.DATE_ADDED,
    val selectionMode: Boolean = false,
    val selectedIds: Set<Long> = emptySet(),
    val showBatchTagDialog: Boolean = false,
    val showBatchCollectionDialog: Boolean = false,
    val selectedTagId: Long? = null,
    val selectedTagName: String? = null,
    val error: String? = null,
    val snackbarMessage: String? = null,
    // Batch import
    val batchImportInProgress: Boolean = false,
    val batchImportProgressText: String = "",
    val batchImportCurrent: Int = 0,
    val batchImportTotal: Int = 0,
    val batchImportResult: BatchImportResult? = null
)

data class BatchImportResult(
    val collectionId: Long,
    val collectionName: String,
    val importedCount: Int,
    val skippedCount: Int
)
