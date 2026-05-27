package com.example.comiclibrary.ui.tags

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comiclibrary.data.repository.TagRepository
import com.example.comiclibrary.domain.model.Tag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagViewModel @Inject constructor(
    private val tagRepository: TagRepository
) : ViewModel() {

    val tags: StateFlow<List<Tag>> = tagRepository.observeAllTags()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _uiState = MutableStateFlow(TagUiState())
    val uiState: StateFlow<TagUiState> = _uiState.asStateFlow()

    fun showCreateDialog() { _uiState.value = _uiState.value.copy(showCreateDialog = true) }
    fun cancelCreateDialog() { _uiState.value = _uiState.value.copy(showCreateDialog = false) }

    fun createTag(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            tagRepository.createTag(name.trim())
            _uiState.value = TagUiState()
        }
    }

    fun startEdit(tagId: Long) { _uiState.value = _uiState.value.copy(editingTagId = tagId) }
    fun cancelEdit() { _uiState.value = _uiState.value.copy(editingTagId = null) }

    fun updateTag(id: Long, name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            tagRepository.updateTag(id, name.trim())
            _uiState.value = TagUiState()
        }
    }

    fun requestDelete(tagId: Long) { _uiState.value = _uiState.value.copy(deleteConfirmTagId = tagId) }
    fun cancelDelete() { _uiState.value = _uiState.value.copy(deleteConfirmTagId = null) }

    fun confirmDelete() {
        val id = _uiState.value.deleteConfirmTagId ?: return
        viewModelScope.launch {
            tagRepository.deleteTag(id)
            _uiState.value = TagUiState()
        }
    }

    // Multi-select

    fun onLongPress(tagId: Long) {
        _uiState.update { it.copy(selectionMode = true, selectedIds = setOf(tagId)) }
    }

    fun onToggleSelection(tagId: Long) {
        _uiState.update { state ->
            if (!state.selectionMode) return@update state
            val newSet = state.selectedIds.toMutableSet()
            if (newSet.contains(tagId)) newSet.remove(tagId) else newSet.add(tagId)
            if (newSet.isEmpty()) state.copy(selectionMode = false, selectedIds = emptySet())
            else state.copy(selectedIds = newSet)
        }
    }

    fun onSelectAll() {
        _uiState.update { state ->
            state.copy(selectedIds = tags.value.map { it.id }.toSet())
        }
    }

    fun exitSelectionMode() {
        _uiState.update { it.copy(selectionMode = false, selectedIds = emptySet()) }
    }

    fun batchDelete() {
        viewModelScope.launch {
            _uiState.value.selectedIds.forEach { id -> tagRepository.deleteTag(id) }
            _uiState.value = TagUiState()
        }
    }
}
