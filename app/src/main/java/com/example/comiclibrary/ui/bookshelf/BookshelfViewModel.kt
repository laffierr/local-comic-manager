package com.example.comiclibrary.ui.bookshelf

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comiclibrary.data.repository.CollectionRepository
import com.example.comiclibrary.data.repository.ComicRepository
import com.example.comiclibrary.data.repository.TagRepository
import com.example.comiclibrary.domain.model.Comic
import com.example.comiclibrary.util.CoverScanner
import com.example.comiclibrary.util.ImageEnumerator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BookshelfViewModel @Inject constructor(
    private val comicRepository: ComicRepository,
    private val coverScanner: CoverScanner,
    private val tagRepository: TagRepository,
    private val collectionRepository: CollectionRepository,
    private val imageEnumerator: ImageEnumerator,
    private val favoriteManager: com.example.comiclibrary.util.FavoriteManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookshelfUiState())
    val uiState: StateFlow<BookshelfUiState> = _uiState.asStateFlow()

    val allTags: StateFlow<List<com.example.comiclibrary.domain.model.Tag>> = tagRepository.observeAllTags()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val allCollections: StateFlow<List<com.example.comiclibrary.domain.model.ComicCollection>> = collectionRepository.observeAllCollections()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val comicsForTagRows: StateFlow<List<Pair<com.example.comiclibrary.domain.model.Tag, List<Comic>>>> =
        allTags.flatMapLatest { tags ->
            if (tags.isEmpty()) kotlinx.coroutines.flow.flowOf(emptyList())
            else combine(tags.map { tag ->
                tagRepository.observeComicsByTag(tag.id).map { comics -> tag to comics.take(10) }
            }) { it.toList() }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun selectTag(tagId: Long?, tagName: String?) {
        _uiState.update { it.copy(selectedTagId = tagId, selectedTagName = tagName) }
    }

    val comics: StateFlow<List<Comic>> = _uiState
        .map { Triple(it.searchQuery, it.sortMode, it.selectedTagId) }
        .flatMapLatest { (query, sort, tagId) ->
            val baseFlow = if (tagId != null) {
                tagRepository.observeComicsByTag(tagId)
            } else {
                comicRepository.observeAllComics()
            }
            baseFlow.map { comics ->
                val filtered = if (query.isBlank()) comics
                else comics.filter { it.title.contains(query, ignoreCase = true) }

                when (sort) {
                    SortMode.DATE_ADDED -> filtered.sortedByDescending { it.addedAtMillis }
                    SortMode.TITLE -> filtered.sortedBy { it.title.lowercase() }
                    SortMode.PAGE_COUNT -> filtered.sortedByDescending { it.pageCount }
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onSortModeChange(mode: SortMode) {
        _uiState.update { it.copy(sortMode = mode) }
    }

    fun dismissSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    // --- Batch selection ---

    fun onLongPress(comicId: Long) {
        _uiState.update {
            it.copy(selectionMode = true, selectedIds = setOf(comicId))
        }
    }

    fun onToggleSelection(comicId: Long) {
        _uiState.update { state ->
            if (!state.selectionMode) return@update state
            val newSet = state.selectedIds.toMutableSet()
            if (newSet.contains(comicId)) newSet.remove(comicId) else newSet.add(comicId)
            if (newSet.isEmpty()) state.copy(selectionMode = false, selectedIds = emptySet())
            else state.copy(selectedIds = newSet)
        }
    }

    fun onSelectAll() {
        _uiState.update { state ->
            state.copy(selectedIds = comics.value.map { it.id }.toSet())
        }
    }

    fun exitSelectionMode() {
        _uiState.update { it.copy(selectionMode = false, selectedIds = emptySet()) }
    }

    fun showBatchTagDialog() {
        _uiState.update { it.copy(showBatchTagDialog = true) }
    }

    fun cancelBatchTagDialog() {
        _uiState.update { it.copy(showBatchTagDialog = false) }
    }

    fun showBatchCollectionDialog() {
        _uiState.update { it.copy(showBatchCollectionDialog = true) }
    }

    fun cancelBatchCollectionDialog() {
        _uiState.update { it.copy(showBatchCollectionDialog = false) }
    }

    fun batchFavorite() {
        viewModelScope.launch {
            _uiState.value.selectedIds.forEach { id ->
                comicRepository.setFavorite(id, true)
            }
            exitSelectionMode()
        }
    }

    fun toggleFavorite(comicId: Long, currentIsFavorite: Boolean) {
        viewModelScope.launch {
            favoriteManager.toggleFavorite(comicId, currentIsFavorite)
        }
    }

    fun batchDelete() {
        viewModelScope.launch {
            val count = _uiState.value.selectedIds.size
            _uiState.value.selectedIds.forEach { id ->
                comicRepository.deleteComic(id)
            }
            _uiState.update { it.copy(
                selectionMode = false,
                selectedIds = emptySet(),
                snackbarMessage = "已删除 $count 本漫画"
            ) }
        }
    }

    fun batchAddTag(tagId: Long) {
        viewModelScope.launch {
            _uiState.value.selectedIds.forEach { comicId ->
                tagRepository.addTagToComic(comicId, tagId)
            }
            _uiState.update { it.copy(showBatchTagDialog = false) }
            exitSelectionMode()
        }
    }

    fun batchCreateAndAddTag(name: String) {
        viewModelScope.launch {
            val tagId = tagRepository.createTag(name)
            _uiState.value.selectedIds.forEach { comicId ->
                tagRepository.addTagToComic(comicId, tagId)
            }
            _uiState.update { it.copy(showBatchTagDialog = false) }
            exitSelectionMode()
        }
    }

    fun batchAddToCollection(collectionId: Long) {
        viewModelScope.launch {
            _uiState.value.selectedIds.forEach { comicId ->
                collectionRepository.addComicToCollection(comicId, collectionId)
            }
            _uiState.update { it.copy(showBatchCollectionDialog = false) }
            exitSelectionMode()
        }
    }

    // --- Add comic ---

    fun onFolderSelected(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val scanResult = coverScanner.scanFolder(uri)
                if (scanResult.pageCount == 0) {
                    _uiState.update { it.copy(error = "文件夹中没有支持的图片文件") }
                    return@launch
                }
                val title = coverScanner.resolveFolderName(uri)
                _uiState.update { it.copy(pendingFolderUri = uri, suggestedTitle = title, showTitleDialog = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "读取文件夹失败: ${e.message}") }
            }
        }
    }

    fun confirmAddComic(title: String) {
        val uri = _uiState.value.pendingFolderUri ?: return
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isAdding = true, showTitleDialog = false) }
            try {
                val scanResult = coverScanner.scanFolder(uri)
                if (scanResult.pageCount == 0) {
                    _uiState.update { it.copy(isAdding = false, pendingFolderUri = null, error = "文件夹中没有支持的图片文件") }
                    return@launch
                }
                comicRepository.addComicFromFolder(uri.toString(), title.ifBlank { "未命名漫画" }, scanResult)
                _uiState.update { it.copy(isAdding = false, pendingFolderUri = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isAdding = false, error = "添加漫画失败: ${e.message}") }
            }
        }
    }

    fun cancelAddComic() {
        _uiState.update { it.copy(pendingFolderUri = null, showTitleDialog = false) }
    }

    fun requestDelete(comicId: Long) {
        _uiState.update { it.copy(deleteConfirmComicId = comicId) }
    }

    fun confirmDelete() {
        val id = _uiState.value.deleteConfirmComicId ?: return
        viewModelScope.launch {
            comicRepository.deleteComic(id)
            _uiState.update { it.copy(deleteConfirmComicId = null, snackbarMessage = "已删除漫画") }
        }
    }

    fun cancelDelete() {
        _uiState.update { it.copy(deleteConfirmComicId = null) }
    }

    // --- Batch import: pick folder → all subfolders imported → collection named after parent ---

    fun onBatchImportFolderSelected(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(
                batchImportInProgress = true,
                batchImportProgressText = "正在扫描文件夹..."
            ) }

            try {
                val parentDoc = DocumentFile.fromTreeUri(context, uri)
                    ?: run {
                        _uiState.update { it.copy(batchImportInProgress = false, error = "无法读取文件夹") }
                        return@launch
                    }

                val collectionName = parentDoc.name ?: "批量导入"
                val subDirs = parentDoc.listFiles().filter { it.isDirectory }
                if (subDirs.isEmpty()) {
                    _uiState.update { it.copy(batchImportInProgress = false, error = "所选文件夹中没有子文件夹") }
                    return@launch
                }

                _uiState.update { it.copy(
                    batchImportTotal = subDirs.size,
                    batchImportCurrent = 0,
                    batchImportProgressText = "正在导入漫画..."
                ) }

                val importedIds = mutableListOf<Long>()
                var skipped = 0

                for ((index, dir) in subDirs.withIndex()) {
                    if (!isActive) {
                        _uiState.update { it.copy(batchImportInProgress = false) }
                        return@launch
                    }

                    _uiState.update { it.copy(batchImportCurrent = index + 1) }

                    try {
                        val dirUri = dir.uri.toString()
                        if (comicRepository.existsByFolderUri(dirUri)) {
                            skipped++
                            continue
                        }

                        val scanResult = coverScanner.scanFolder(dir.uri)
                        if (scanResult.pageCount == 0) {
                            skipped++
                            continue
                        }

                        val comicName = dir.name ?: "未命名"
                        val comicId = comicRepository.addComicFromFolder(dirUri, comicName, scanResult)
                        importedIds.add(comicId)
                    } catch (e: Exception) {
                        skipped++
                    }
                }

                // Create collection named after parent folder
                val collectionId = if (importedIds.isNotEmpty()) {
                    val id = collectionRepository.createCollection(
                        name = collectionName,
                        description = "共 ${importedIds.size} 本漫画"
                    )
                    for (comicId in importedIds) {
                        collectionRepository.addComicToCollection(comicId, id)
                    }
                    id
                } else {
                    -1L
                }

                _uiState.update { it.copy(
                    batchImportInProgress = false,
                    batchImportResult = BatchImportResult(
                        collectionId = collectionId,
                        collectionName = collectionName,
                        importedCount = importedIds.size,
                        skippedCount = skipped
                    )
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    batchImportInProgress = false,
                    error = "导入失败: ${e.message}"
                ) }
            }
        }
    }

    fun dismissBatchImportResult() {
        _uiState.update { it.copy(batchImportResult = null) }
    }

    fun cancelBatchImport() {
        _uiState.update { it.copy(batchImportInProgress = false) }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }
}
