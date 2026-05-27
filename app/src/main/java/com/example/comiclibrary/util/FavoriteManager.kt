package com.example.comiclibrary.util

import com.example.comiclibrary.data.repository.CollectionRepository
import com.example.comiclibrary.data.repository.ComicRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteManager @Inject constructor(
    private val comicRepository: ComicRepository,
    private val collectionRepository: CollectionRepository
) {
    companion object {
        const val FAVORITES_COLLECTION_NAME = "我的最爱"
    }

    suspend fun getOrCreateFavoritesCollectionId(): Long {
        val existing = collectionRepository.findCollectionByName(FAVORITES_COLLECTION_NAME)
        if (existing != null) return existing
        return collectionRepository.createCollection(
            FAVORITES_COLLECTION_NAME,
            "自动创建的最爱收藏夹"
        )
    }

    suspend fun toggleFavorite(comicId: Long, currentIsFavorite: Boolean) {
        val newState = !currentIsFavorite
        comicRepository.setFavorite(comicId, newState)
        val favId = getOrCreateFavoritesCollectionId()
        if (newState) {
            collectionRepository.addComicToCollection(comicId, favId)
        } else {
            collectionRepository.removeComicFromCollection(comicId, favId)
        }
    }
}
