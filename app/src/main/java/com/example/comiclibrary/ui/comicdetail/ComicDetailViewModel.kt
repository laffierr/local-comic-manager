package com.example.comiclibrary.ui.comicdetail

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.Immutable
import com.example.comiclibrary.data.repository.CollectionRepository
import com.example.comiclibrary.data.repository.ComicRepository
import com.example.comiclibrary.data.repository.TagRepository
import com.example.comiclibrary.util.FavoriteManager
import com.example.comiclibrary.domain.model.Comic
import com.example.comiclibrary.domain.model.ComicCollection
import com.example.comiclibrary.domain.model.Tag
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@Immutable
data class ComicDetailUiState(
    val comic: Comic? = null,
    val allCollections: List<ComicCollection> = emptyList(),
    val allTags: List<Tag> = emptyList(),
    val memberCollectionIds: Set<Long> = emptySet(),
    val memberTagIds: Set<Long> = emptySet(),
    val isLoading: Boolean = true,
    val showDeleteDialog: Boolean = false,
    val showNewTagDialog: Boolean = false,
    val showCreateCollectionDialog: Boolean = false
)

private data class DialogState(
    val showDeleteDialog: Boolean = false,
    val showNewTagDialog: Boolean = false,
    val showCreateCollectionDialog: Boolean = false
)

@HiltViewModel
class ComicDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val comicRepository: ComicRepository,
    private val collectionRepository: CollectionRepository,
    private val tagRepository: TagRepository,
    private val favoriteManager: FavoriteManager
) : ViewModel() {

    private val comicId: Long = savedStateHandle.get<Long>("comicId") ?: -1L

    private val _dialogState = MutableStateFlow(DialogState())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ComicDetailUiState> = combine(
        combine(
            comicRepository.observeComicById(comicId),
            collectionRepository.observeAllCollections(),
            collectionRepository.observeCollectionIdsForComic(comicId),
            tagRepository.observeAllTags(),
            tagRepository.observeTagsForComic(comicId)
        ) { comic, collections, memberIds, tags, comicTags ->
            listOf(comic, collections, memberIds, tags, comicTags)
        },
        _dialogState
    ) { data, dialogState ->
        @Suppress("UNCHECKED_CAST")
        ComicDetailUiState(
            comic = data[0] as Comic?,
            allCollections = data[1] as List<ComicCollection>,
            allTags = data[3] as List<Tag>,
            memberCollectionIds = (data[2] as List<Long>).toSet(),
            memberTagIds = (data[4] as List<Tag>).map { it.id }.toSet(),
            isLoading = false,
            showDeleteDialog = dialogState.showDeleteDialog,
            showNewTagDialog = dialogState.showNewTagDialog,
            showCreateCollectionDialog = dialogState.showCreateCollectionDialog
        )
    }.catch {
        emit(ComicDetailUiState(isLoading = false))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ComicDetailUiState())

    fun toggleFavorite() {
        val comic = uiState.value.comic ?: return
        viewModelScope.launch {
            favoriteManager.toggleFavorite(comic.id, comic.isFavorite)
        }
    }

    fun toggleCollection(collectionId: Long, currentlyMember: Boolean) {
        viewModelScope.launch {
            if (currentlyMember) collectionRepository.removeComicFromCollection(comicId, collectionId)
            else collectionRepository.addComicToCollection(comicId, collectionId)
        }
    }

    fun toggleTag(tagId: Long, currentlyMember: Boolean) {
        viewModelScope.launch {
            if (currentlyMember) tagRepository.removeTagFromComic(comicId, tagId)
            else tagRepository.addTagToComic(comicId, tagId)
        }
    }

    fun createAndAddTag(name: String) {
        viewModelScope.launch {
            val tagId = tagRepository.createTag(name)
            tagRepository.addTagToComic(comicId, tagId)
            _dialogState.update { it.copy(showNewTagDialog = false) }
        }
    }

    fun showNewTagDialog() {
        _dialogState.update { it.copy(showNewTagDialog = true) }
    }

    fun cancelNewTagDialog() {
        _dialogState.update { it.copy(showNewTagDialog = false) }
    }

    fun showCreateCollectionDialog() {
        _dialogState.update { it.copy(showCreateCollectionDialog = true) }
    }

    fun cancelCreateCollectionDialog() {
        _dialogState.update { it.copy(showCreateCollectionDialog = false) }
    }

    fun createAndAddToCollection(name: String) {
        viewModelScope.launch {
            val collectionId = collectionRepository.createCollection(name.ifBlank { "未命名收藏夹" }, "")
            collectionRepository.addComicToCollection(comicId, collectionId)
            _dialogState.update { it.copy(showCreateCollectionDialog = false) }
        }
    }

    fun showDeleteDialog() {
        _dialogState.update { it.copy(showDeleteDialog = true) }
    }

    fun cancelDelete() {
        _dialogState.update { it.copy(showDeleteDialog = false) }
    }

    fun deleteComic() {
        viewModelScope.launch {
            comicRepository.deleteComic(comicId)
            _dialogState.update { it.copy(showDeleteDialog = false) }
        }
    }

    fun formatDate(millis: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return sdf.format(Date(millis))
    }
}
