package com.example.comiclibrary.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.comiclibrary.data.local.entity.ComicTagCrossRef
import com.example.comiclibrary.data.local.entity.ComicWithCollections
import com.example.comiclibrary.data.local.entity.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun observeAllTags(): Flow<List<TagEntity>>

    @Query("UPDATE tags SET name = :name WHERE id = :id")
    suspend fun updateTag(id: Long, name: String)

    @Insert
    suspend fun insertTag(tag: TagEntity): Long

    @Delete
    suspend fun deleteTag(tag: TagEntity)

    @Query("DELETE FROM tags WHERE id = :id")
    suspend fun deleteTagById(id: Long)

    @Insert
    suspend fun addTagToComic(crossRef: ComicTagCrossRef)

    @Delete
    suspend fun removeTagFromComic(crossRef: ComicTagCrossRef)

    @Transaction
    @Query(
        """
        SELECT c.* FROM comics c
        INNER JOIN comic_tag_cross_ref ct ON c.id = ct.comicId
        WHERE ct.tagId = :tagId
        ORDER BY c.addedAtMillis DESC
        """
    )
    fun observeComicsByTag(tagId: Long): Flow<List<ComicWithCollections>>

    @Query("SELECT tagId FROM comic_tag_cross_ref WHERE comicId = :comicId")
    fun observeTagIdsForComic(comicId: Long): Flow<List<Long>>

    @Query(
        """
        SELECT t.* FROM tags t
        INNER JOIN comic_tag_cross_ref ct ON t.id = ct.tagId
        WHERE ct.comicId = :comicId
        ORDER BY t.name ASC
        """
    )
    fun observeTagsForComic(comicId: Long): Flow<List<TagEntity>>

    @Transaction
    @Query(
        """
        SELECT c.* FROM comics c
        INNER JOIN comic_tag_cross_ref ct ON c.id = ct.comicId
        WHERE ct.tagId IN (:tagIds)
        GROUP BY c.id
        HAVING COUNT(DISTINCT ct.tagId) = :tagCount
        ORDER BY c.addedAtMillis DESC
        """
    )
    fun observeComicsByTags(tagIds: List<Long>, tagCount: Int): Flow<List<ComicWithCollections>>
}
