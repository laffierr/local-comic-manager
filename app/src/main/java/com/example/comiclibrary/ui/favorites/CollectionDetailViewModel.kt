package com.example.comiclibrary.ui.favorites

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comiclibrary.data.repository.CollectionRepository
import com.example.comiclibrary.domain.model.Comic
import com.example.comiclibrary.domain.model.ComicCollection
import com.example.comiclibrary.util.FavoriteManager
import com.example.comiclibrary.util.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val collectionRepository: CollectionRepository,
    private val settingsManager: SettingsManager,
    private val favoriteManager: FavoriteManager
) : ViewModel() {

    private val collectionId: Long = savedStateHandle.get<Long>("collectionId") ?: -1L

    val collection: StateFlow<ComicCollection?> =
        collectionRepository.observeCollectionById(collectionId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val comics: StateFlow<List<Comic>> =
        collectionRepository.observeComicsInCollection(collectionId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val gridColumns: StateFlow<Int> = settingsManager.gridColumns
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 3)

    fun cycleGridColumns() {
        viewModelScope.launch {
            val current = gridColumns.value
            val next = when (current) { 3 -> 4; 4 -> 5; 5 -> 3; else -> 3 }
            settingsManager.setGridColumns(next)
        }
    }

    fun removeComicFromCollection(comicId: Long) {
        viewModelScope.launch {
            collectionRepository.removeComicFromCollection(comicId, collectionId)
        }
    }

    fun updateCollection(name: String, description: String) {
        viewModelScope.launch {
            collectionRepository.updateCollection(collectionId, name, description)
        }
    }

    fun toggleFavorite(comicId: Long, currentIsFavorite: Boolean) {
        viewModelScope.launch {
            favoriteManager.toggleFavorite(comicId, currentIsFavorite)
        }
    }
}
