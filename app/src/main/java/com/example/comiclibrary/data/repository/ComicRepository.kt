package com.example.comiclibrary.data.repository

import com.example.comiclibrary.domain.model.Comic
import com.example.comiclibrary.domain.model.CoverScanResult
import kotlinx.coroutines.flow.Flow

interface ComicRepository {
    fun observeAllComics(): Flow<List<Comic>>
    fun observeComicById(id: Long): Flow<Comic?>
    fun observeRandomComics(limit: Int): Flow<List<Comic>>
    fun observeRecentComics(limit: Int): Flow<List<Comic>>
    fun observeComicCount(): Flow<Int>

    fun observeFavoriteComics(): Flow<List<Comic>>
    fun observeFavoriteCount(): Flow<Int>

    suspend fun addComicFromFolder(folderUri: String, title: String, scanResult: CoverScanResult): Long
    suspend fun updateComicTitle(id: Long, title: String)
    suspend fun setFavorite(id: Long, isFavorite: Boolean)
    suspend fun deleteComic(id: Long)
    suspend fun updateLastReadAt(comicId: Long)
    suspend fun existsByFolderUri(folderUri: String): Boolean
}
