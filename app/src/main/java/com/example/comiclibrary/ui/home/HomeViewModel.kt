package com.example.comiclibrary.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comiclibrary.data.repository.ComicRepository
import com.example.comiclibrary.util.Constants
import com.example.comiclibrary.util.FavoriteManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val comicRepository: ComicRepository,
    private val favoriteManager: FavoriteManager
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        comicRepository.observeRandomComics(Constants.RANDOM_PICK_COUNT),
        comicRepository.observeRecentComics(Constants.RECENT_COUNT),
        comicRepository.observeComicCount()
    ) { random, recent, count ->
        HomeUiState(randomComics = random, recentComics = recent, totalCount = count, isLoading = false)
    }.catch { e ->
        emit(HomeUiState(isLoading = false, error = e.message ?: "加载失败"))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun toggleFavorite(comicId: Long, currentIsFavorite: Boolean) {
        viewModelScope.launch {
            favoriteManager.toggleFavorite(comicId, currentIsFavorite)
        }
    }
}
