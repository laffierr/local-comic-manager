package com.example.comiclibrary.data.repository

import com.example.comiclibrary.domain.model.Comic
import com.example.comiclibrary.domain.model.ComicCollection
import kotlinx.coroutines.flow.Flow

interface CollectionRepository {
    fun observeAllCollections(): Flow<List<ComicCollection>>
    fun observeCollectionById(id: Long): Flow<ComicCollection?>
    fun observeComicsInCollection(collectionId: Long): Flow<List<Comic>>
    fun observeCollectionIdsForComic(comicId: Long): Flow<List<Long>>

    suspend fun createCollection(name: String, description: String): Long
    suspend fun updateCollection(id: Long, name: String, description: String)
    suspend fun deleteCollection(id: Long)

    suspend fun addComicToCollection(comicId: Long, collectionId: Long)
    suspend fun removeComicFromCollection(comicId: Long, collectionId: Long)
    suspend fun findCollectionByName(name: String): Long?
}
