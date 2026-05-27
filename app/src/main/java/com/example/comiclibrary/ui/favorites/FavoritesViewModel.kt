package com.example.comiclibrary.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comiclibrary.data.repository.CollectionRepository
import com.example.comiclibrary.domain.model.ComicCollection
import com.example.comiclibrary.util.FavoriteManager
import com.example.comiclibrary.util.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val collectionRepository: CollectionRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    val pinnedIds: StateFlow<Set<String>> = settingsManager.pinnedCollectionIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    val collections: StateFlow<List<ComicCollection>> = combine(
        collectionRepository.observeAllCollections().catch { emit(emptyList()) },
        settingsManager.pinnedCollectionIds
    ) { list, pinned ->
        list.sortedByDescending { it.name == FavoriteManager.FAVORITES_COLLECTION_NAME || it.id.toString() in pinned }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    fun dismissSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun showCreateDialog() { _uiState.value = _uiState.value.copy(showCreateDialog = true) }
    fun cancelCreateDialog() { _uiState.value = _uiState.value.copy(showCreateDialog = false) }

    fun createCollection(name: String, description: String) {
        viewModelScope.launch {
            collectionRepository.createCollection(name.ifBlank { "未命名收藏夹" }, description)
            _uiState.value = FavoritesUiState()
        }
    }

    fun showMenu(collectionId: Long) {
        _uiState.value = _uiState.value.copy(menuCollectionId = collectionId)
    }

    fun dismissMenu() {
        _uiState.value = _uiState.value.copy(menuCollectionId = null)
    }

    fun togglePin(collectionId: Long) {
        viewModelScope.launch {
            settingsManager.togglePinnedCollection(collectionId)
        }
        _uiState.value = FavoritesUiState()
    }

    fun enterSelectionMode(collectionId: Long) {
        _uiState.value = _uiState.value.copy(selectionMode = true, selectedIds = setOf(collectionId), menuCollectionId = null)
    }

    fun onLongPress(collectionId: Long) {
        _uiState.value = _uiState.value.copy(selectionMode = true, selectedIds = setOf(collectionId))
    }

    fun onToggleSelection(collectionId: Long) {
        val state = _uiState.value
        if (!state.selectionMode) return
        val target = collections.value.find { it.id == collectionId }
        if (target?.name == FavoriteManager.FAVORITES_COLLECTION_NAME) return
        val newSet = state.selectedIds.toMutableSet()
        if (newSet.contains(collectionId)) newSet.remove(collectionId) else newSet.add(collectionId)
        if (newSet.isEmpty()) {
            _uiState.value = state.copy(selectionMode = false, selectedIds = emptySet())
        } else {
            _uiState.value = state.copy(selectedIds = newSet)
        }
    }

    fun exitSelectionMode() {
        _uiState.value = _uiState.value.copy(selectionMode = false, selectedIds = emptySet())
    }

    fun onSelectAll() {
        _uiState.value = _uiState.value.copy(
            selectedIds = collections.value
                .filter { it.name != FavoriteManager.FAVORITES_COLLECTION_NAME }
                .map { it.id }.toSet()
        )
    }

    fun batchDelete() {
        viewModelScope.launch {
            val count = _uiState.value.selectedIds.size
            _uiState.value.selectedIds.forEach { id -> collectionRepository.deleteCollection(id) }
            _uiState.value = FavoritesUiState().copy(snackbarMessage = "已删除 $count 个收藏夹")
        }
    }

    fun requestDelete(collectionId: Long) {
        _uiState.value = _uiState.value.copy(deleteConfirmCollectionId = collectionId)
    }

    fun confirmDelete() {
        val id = _uiState.value.deleteConfirmCollectionId ?: return
        val target = collections.value.find { it.id == id }
        if (target?.name == FavoriteManager.FAVORITES_COLLECTION_NAME) {
            _uiState.value = FavoritesUiState()
            return
        }
        viewModelScope.launch {
            collectionRepository.deleteCollection(id)
            _uiState.value = FavoritesUiState().copy(snackbarMessage = "已删除收藏夹")
        }
    }

    fun cancelDelete() { _uiState.value = _uiState.value.copy(deleteConfirmCollectionId = null) }

    fun startEdit(collectionId: Long) {
        _uiState.value = _uiState.value.copy(editingCollectionId = collectionId)
    }

    fun updateCollection(id: Long, name: String, description: String) {
        viewModelScope.launch {
            collectionRepository.updateCollection(id, name, description)
            _uiState.value = _uiState.value.copy(editingCollectionId = null)
        }
    }

    fun cancelEdit() {
        _uiState.value = _uiState.value.copy(editingCollectionId = null)
    }
}
