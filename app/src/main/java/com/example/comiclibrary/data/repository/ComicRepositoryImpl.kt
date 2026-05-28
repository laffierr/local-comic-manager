package com.example.comiclibrary.data.repository

import android.net.Uri
import com.example.comiclibrary.data.local.dao.ComicDao
import com.example.comiclibrary.data.local.entity.ComicEntity
import com.example.comiclibrary.data.model.toDomain
import com.example.comiclibrary.domain.model.Comic
import com.example.comiclibrary.domain.model.CoverScanResult
import com.example.comiclibrary.util.ImageEnumerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ComicRepositoryImpl @Inject constructor(
    private val comicDao: ComicDao,
    private val imageEnumerator: ImageEnumerator
) : ComicRepository {

    override fun observeAllComics(): Flow<List<Comic>> =
        comicDao.observeAllComics().map { list -> list.map { it.toDomain() } }
            .distinctUntilChanged()

    override fun observeComicById(id: Long): Flow<Comic?> =
        comicDao.observeComicById(id).map { it?.toDomain() }
            .distinctUntilChanged()

    override fun observeRandomComics(limit: Int): Flow<List<Comic>> =
        comicDao.observeRandomComics(limit).map { list -> list.map { it.toDomain() } }
            .distinctUntilChanged()

    override fun observeRecentComics(limit: Int): Flow<List<Comic>> =
        comicDao.observeRecentComics(limit).map { list -> list.map { it.toDomain() } }
            .distinctUntilChanged()

    override fun observeFavoriteComics(): Flow<List<Comic>> =
        comicDao.observeFavoriteComics().map { list -> list.map { it.toDomain() } }
            .distinctUntilChanged()

    override fun observeComicCount(): Flow<Int> = comicDao.observeComicCount()
        .distinctUntilChanged()
    override fun observeFavoriteCount(): Flow<Int> = comicDao.observeFavoriteCount()
        .distinctUntilChanged()

    override suspend fun addComicFromFolder(
        folderUri: String, title: String, scanResult: CoverScanResult
    ): Long {
        val entity = ComicEntity(
            title = title,
            folderUri = folderUri,
            coverUri = scanResult.coverUri,
            pageCount = scanResult.pageCount,
            addedAtMillis = System.currentTimeMillis()
        )
        return comicDao.insertComic(entity)
    }

    override suspend fun updateComicTitle(id: Long, title: String) {
        comicDao.updateComicTitle(id, title)
    }

    override suspend fun setFavorite(id: Long, isFavorite: Boolean) {
        comicDao.setFavorite(id, isFavorite)
    }

    override suspend fun deleteComic(id: Long) {
        comicDao.deleteComicById(id)
    }

    override suspend fun updateLastReadAt(comicId: Long) {
        comicDao.updateLastReadAt(comicId, System.currentTimeMillis())
    }

    override suspend fun existsByFolderUri(folderUri: String): Boolean {
        return comicDao.existsByFolderUri(folderUri)
    }

    override suspend fun refreshCover(id: Long, folderUri: String) {
        val files = imageEnumerator.listImageFiles(Uri.parse(folderUri))
        val newCover = files.firstOrNull()?.uri?.toString()
        comicDao.updateCoverUri(id, newCover)
    }
}
