package com.example.comiclibrary.ui.reader

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comiclibrary.data.repository.ComicRepository
import com.example.comiclibrary.util.ImageEnumerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val comicRepository: ComicRepository,
    private val imageEnumerator: ImageEnumerator
) : ViewModel() {

    private val comicId: Long = savedStateHandle.get<Long>("comicId") ?: -1L

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    init {
        loadComic()
    }

    private fun loadComic() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val comic = withTimeout(2000L) {
                    comicRepository.observeComicById(comicId).first { it != null }
                }

                if (comic == null) {
                    _uiState.update { it.copy(isLoading = false, error = "漫画未找到") }
                    return@launch
                }

                val folderUri = Uri.parse(comic.folderUri)
                val uris = imageEnumerator.listImageUris(folderUri)

                _uiState.update {
                    it.copy(comic = comic, imageUris = uris, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "加载失败") }
            }
        }
    }

    fun setCurrentPage(page: Int) {
        _uiState.update { it.copy(currentPage = page) }
    }

    fun toggleReadingMode() {
        _uiState.update { it.copy(readingMode = if (it.readingMode == ReaderMode.SCROLL) ReaderMode.PAGE else ReaderMode.SCROLL) }
    }

    fun toggleOverlay() {
        _uiState.update { it.copy(showOverlay = !it.showOverlay) }
    }

    override fun onCleared() {
        super.onCleared()
        if (comicId > 0) {
            viewModelScope.launch(Dispatchers.IO) {
                comicRepository.updateLastReadAt(comicId)
            }
        }
    }
}
