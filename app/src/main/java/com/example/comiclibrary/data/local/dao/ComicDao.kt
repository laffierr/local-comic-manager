package com.example.comiclibrary.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.comiclibrary.data.local.entity.ComicEntity
import com.example.comiclibrary.data.local.entity.ComicWithCollections
import kotlinx.coroutines.flow.Flow

@Dao
interface ComicDao {

    @Transaction
    @Query("SELECT * FROM comics ORDER BY addedAtMillis DESC")
    fun observeAllComics(): Flow<List<ComicWithCollections>>

    @Transaction
    @Query("SELECT * FROM comics WHERE id = :id")
    fun observeComicById(id: Long): Flow<ComicWithCollections?>

    @Transaction
    @Query("SELECT * FROM comics ORDER BY RANDOM() LIMIT :limit")
    fun observeRandomComics(limit: Int): Flow<List<ComicWithCollections>>

    @Transaction
    @Query("SELECT * FROM comics ORDER BY addedAtMillis DESC LIMIT :limit")
    fun observeRecentComics(limit: Int): Flow<List<ComicWithCollections>>

    @Transaction
    @Query("SELECT * FROM comics WHERE isFavorite = 1 ORDER BY addedAtMillis DESC")
    fun observeFavoriteComics(): Flow<List<ComicWithCollections>>

    @Query("SELECT COUNT(*) FROM comics")
    fun observeComicCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM comics WHERE isFavorite = 1")
    fun observeFavoriteCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComic(comic: ComicEntity): Long

    @Update
    suspend fun updateComic(comic: ComicEntity)

    @Delete
    suspend fun deleteComic(comic: ComicEntity)

    @Query("DELETE FROM comics WHERE id = :id")
    suspend fun deleteComicById(id: Long)

    @Query("UPDATE comics SET title = :title WHERE id = :id")
    suspend fun updateComicTitle(id: Long, title: String)

    @Query("UPDATE comics SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)

    @Query("UPDATE comics SET lastReadAtMillis = :timeMillis WHERE id = :id")
    suspend fun updateLastReadAt(id: Long, timeMillis: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM comics WHERE folderUri = :folderUri)")
    suspend fun existsByFolderUri(folderUri: String): Boolean
}
