package com.example.comiclibrary.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.comiclibrary.data.local.entity.ComicCollectionCrossRef
import com.example.comiclibrary.data.local.entity.ComicWithCollections
import kotlinx.coroutines.flow.Flow

@Dao
interface ComicCollectionDao {

    @Insert
    suspend fun addComicToCollection(crossRef: ComicCollectionCrossRef)

    @Delete
    suspend fun removeComicFromCollection(crossRef: ComicCollectionCrossRef)

    @Transaction
    @Query(
        """
        SELECT c.* FROM comics c
        INNER JOIN comic_collection_cross_ref cc ON c.id = cc.comicId
        WHERE cc.collectionId = :collectionId
        ORDER BY c.addedAtMillis DESC
        """
    )
    fun observeComicsInCollection(collectionId: Long): Flow<List<ComicWithCollections>>

    @Query("SELECT collectionId FROM comic_collection_cross_ref WHERE comicId = :comicId")
    fun observeCollectionIdsForComic(comicId: Long): Flow<List<Long>>

    @Query("SELECT COUNT(*) FROM comic_collection_cross_ref WHERE collectionId = :collectionId")
    fun observeComicCountInCollection(collectionId: Long): Flow<Int>
}
