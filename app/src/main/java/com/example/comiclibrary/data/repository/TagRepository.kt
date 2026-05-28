package com.example.comiclibrary.data.repository

import com.example.comiclibrary.domain.model.Comic
import com.example.comiclibrary.domain.model.Tag
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    fun observeAllTags(): Flow<List<Tag>>
    fun observeTagsForComic(comicId: Long): Flow<List<Tag>>
    fun observeComicsByTag(tagId: Long): Flow<List<Comic>>
    fun observeComicsByTags(tagIds: Set<Long>): Flow<List<Comic>>

    suspend fun createTag(name: String): Long
    suspend fun updateTag(id: Long, name: String)
    suspend fun deleteTag(id: Long)
    suspend fun addTagToComic(comicId: Long, tagId: Long)
    suspend fun removeTagFromComic(comicId: Long, tagId: Long)
}
