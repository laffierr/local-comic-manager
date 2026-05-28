package com.example.comiclibrary.ui.comicdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.Immutable
import com.example.comiclibrary.data.repository.ComicRepository
import com.example.comiclibrary.data.repository.TagRepository
import com.example.comiclibrary.util.FavoriteManager
import com.example.comiclibrary.domain.model.Comic
import com.example.comiclibrary.domain.model.Tag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
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
    val allTags: List<Tag> = emptyList(),
    val memberTagIds: Set<Long> = emptySet(),
    val isLoading: Boolean = true,
    val showDeleteDialog: Boolean = false,
    val showNewTagDialog: Boolean = false
)

private data class DialogState(
    val showDeleteDialog: Boolean = false,
    val showNewTagDialog: Boolean = false
)

@HiltViewModel
class ComicDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val comicRepository: ComicRepository,
    private val tagRepository: TagRepository,
    private val favoriteManager: FavoriteManager
) : ViewModel() {

    private val comicId: Long = savedStateHandle.get<Long>("comicId") ?: -1L

    private val _dialogState = MutableStateFlow(DialogState())

    init {
        viewModelScope.launch {
            val comic = comicRepository.observeComicById(comicId).first()
            if (comic != null && comic.coverUri.isNullOrBlank()) {
                comicRepository.refreshCover(comic.id, comic.folderUri)
            }
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ComicDetailUiState> = combine(
        combine(
            comicRepository.observeComicById(comicId),
            tagRepository.observeAllTags(),
            tagRepository.observeTagsForComic(comicId)
        ) { comic, tags, comicTags ->
            Triple(comic, tags, comicTags)
        },
        _dialogState
    ) { data, dialogState ->
        val (comic, tags, comicTags) = data
        ComicDetailUiState(
            comic = comic,
            allTags = tags,
            memberTagIds = comicTags.map { it.id }.toSet(),
            isLoading = false,
            showDeleteDialog = dialogState.showDeleteDialog,
            showNewTagDialog = dialogState.showNewTagDialog
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
