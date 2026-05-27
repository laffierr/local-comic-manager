package com.example.comiclibrary.ui.tagcomics

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comiclibrary.data.repository.ComicRepository
import com.example.comiclibrary.data.repository.TagRepository
import com.example.comiclibrary.domain.model.Comic
import com.example.comiclibrary.util.FavoriteManager
import com.example.comiclibrary.util.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TagComicsSortMode { DATE_ADDED, TITLE, PAGE_COUNT }

data class TagComicsUiState(
    val selectionMode: Boolean = false,
    val selectedIds: Set<Long> = emptySet(),
    val deleteConfirmComicId: Long? = null
)

@HiltViewModel
class TagComicsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val comicRepository: ComicRepository,
    private val tagRepository: TagRepository,
    private val settingsManager: SettingsManager,
    private val favoriteManager: FavoriteManager
) : ViewModel() {

    private val tagId: Long = savedStateHandle.get<Long>("tagId") ?: -1L
    private val tagNameArg: String = savedStateHandle.get<String>("tagName") ?: "全部漫画"

    val tagName: StateFlow<String> = kotlinx.coroutines.flow.flowOf(tagNameArg)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), tagNameArg)

    private val _sortMode = MutableStateFlow(TagComicsSortMode.DATE_ADDED)
    val sortMode: StateFlow<TagComicsSortMode> = _sortMode.asStateFlow()

    private val _uiState = MutableStateFlow(TagComicsUiState())
    val uiState: StateFlow<TagComicsUiState> = _uiState.asStateFlow()

    fun setSortMode(mode: TagComicsSortMode) {
        _sortMode.value = mode
    }

    private val rawComics: StateFlow<List<Comic>> =
        (if (tagId <= 0) comicRepository.observeAllComics()
         else tagRepository.observeComicsByTag(tagId))
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val sortedComics: StateFlow<List<Comic>> = combine(rawComics, _sortMode) { list, mode ->
        when (mode) {
            TagComicsSortMode.DATE_ADDED -> list.sortedByDescending { it.addedAtMillis }
            TagComicsSortMode.TITLE -> list.sortedBy { it.title.lowercase() }
            TagComicsSortMode.PAGE_COUNT -> list.sortedByDescending { it.pageCount }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val gridColumns: StateFlow<Int> = settingsManager.gridColumns
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 3)

    fun toggleFavorite(comicId: Long, currentIsFavorite: Boolean) {
        viewModelScope.launch {
            favoriteManager.toggleFavorite(comicId, currentIsFavorite)
        }
    }

    // --- Selection mode ---

    fun onLongPress(comicId: Long) {
        _uiState.update { it.copy(selectionMode = true, selectedIds = setOf(comicId)) }
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
            state.copy(selectedIds = sortedComics.value.map { it.id }.toSet())
        }
    }

    fun exitSelectionMode() {
        _uiState.update { it.copy(selectionMode = false, selectedIds = emptySet()) }
    }

    fun batchDelete() {
        viewModelScope.launch {
            _uiState.value.selectedIds.forEach { id ->
                comicRepository.deleteComic(id)
            }
            exitSelectionMode()
        }
    }

    fun batchFavorite() {
        viewModelScope.launch {
            _uiState.value.selectedIds.forEach { id ->
                comicRepository.setFavorite(id, true)
            }
            exitSelectionMode()
        }
    }

    // --- Single delete ---

    fun requestDelete(comicId: Long) {
        _uiState.update { it.copy(deleteConfirmComicId = comicId) }
    }

    fun confirmDelete() {
        val id = _uiState.value.deleteConfirmComicId ?: return
        viewModelScope.launch {
            comicRepository.deleteComic(id)
            _uiState.update { it.copy(deleteConfirmComicId = null) }
        }
    }

    fun cancelDelete() {
        _uiState.update { it.copy(deleteConfirmComicId = null) }
    }
}
