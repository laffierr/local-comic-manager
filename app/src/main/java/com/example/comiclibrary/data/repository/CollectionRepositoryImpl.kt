package com.example.comiclibrary.data.repository

import com.example.comiclibrary.data.local.dao.CollectionDao
import com.example.comiclibrary.data.local.dao.ComicCollectionDao
import com.example.comiclibrary.data.local.entity.CollectionEntity
import com.example.comiclibrary.data.local.entity.ComicCollectionCrossRef
import com.example.comiclibrary.data.model.toDomain
import com.example.comiclibrary.domain.model.Comic
import com.example.comiclibrary.domain.model.ComicCollection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollectionRepositoryImpl @Inject constructor(
    private val collectionDao: CollectionDao,
    private val comicCollectionDao: ComicCollectionDao
) : CollectionRepository {

    override fun observeAllCollections(): Flow<List<ComicCollection>> =
        collectionDao.observeAllCollections().map { entities ->
            entities.map { it.toDomain() }
        }.distinctUntilChanged()

    override fun observeCollectionById(id: Long): Flow<ComicCollection?> =
        collectionDao.observeCollectionById(id).map { it?.toDomain() }
            .distinctUntilChanged()

    override fun observeComicsInCollection(collectionId: Long): Flow<List<Comic>> =
        comicCollectionDao.observeComicsInCollection(collectionId).map { list ->
            list.map { it.toDomain() }
        }.distinctUntilChanged()

    override fun observeCollectionIdsForComic(comicId: Long): Flow<List<Long>> =
        comicCollectionDao.observeCollectionIdsForComic(comicId)
            .distinctUntilChanged()

    override suspend fun createCollection(name: String, description: String): Long {
        return collectionDao.insertCollection(
            CollectionEntity(name = name, description = description)
        )
    }

    override suspend fun updateCollection(id: Long, name: String, description: String) {
        collectionDao.updateCollection(
            CollectionEntity(id = id, name = name, description = description)
        )
    }

    override suspend fun deleteCollection(id: Long) {
        collectionDao.deleteCollectionById(id)
    }

    override suspend fun addComicToCollection(comicId: Long, collectionId: Long) {
        comicCollectionDao.addComicToCollection(
            ComicCollectionCrossRef(comicId = comicId, collectionId = collectionId)
        )
    }

    override suspend fun removeComicFromCollection(comicId: Long, collectionId: Long) {
        comicCollectionDao.removeComicFromCollection(
            ComicCollectionCrossRef(comicId = comicId, collectionId = collectionId)
        )
    }

    override suspend fun findCollectionByName(name: String): Long? {
        return collectionDao.findByName(name)?.id
    }
}
