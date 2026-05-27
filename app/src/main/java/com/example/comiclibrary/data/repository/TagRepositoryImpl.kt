package com.example.comiclibrary.data.repository

import com.example.comiclibrary.data.local.dao.TagDao
import com.example.comiclibrary.data.local.entity.ComicTagCrossRef
import com.example.comiclibrary.data.local.entity.TagEntity
import com.example.comiclibrary.data.model.toDomain
import com.example.comiclibrary.domain.model.Comic
import com.example.comiclibrary.domain.model.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagRepositoryImpl @Inject constructor(
    private val tagDao: TagDao
) : TagRepository {

    override fun observeAllTags(): Flow<List<Tag>> =
        tagDao.observeAllTags().map { list -> list.map { it.toDomain() } }
            .distinctUntilChanged()

    override fun observeTagsForComic(comicId: Long): Flow<List<Tag>> =
        tagDao.observeTagsForComic(comicId).map { list -> list.map { it.toDomain() } }
            .distinctUntilChanged()

    override fun observeComicsByTag(tagId: Long): Flow<List<Comic>> =
        tagDao.observeComicsByTag(tagId).map { list -> list.map { it.toDomain() } }
            .distinctUntilChanged()

    override suspend fun createTag(name: String): Long {
        return tagDao.insertTag(TagEntity(name = name))
    }

    override suspend fun updateTag(id: Long, name: String) {
        tagDao.updateTag(id, name)
    }

    override suspend fun deleteTag(id: Long) {
        tagDao.deleteTagById(id)
    }

    override suspend fun addTagToComic(comicId: Long, tagId: Long) {
        tagDao.addTagToComic(ComicTagCrossRef(comicId = comicId, tagId = tagId))
    }

    override suspend fun removeTagFromComic(comicId: Long, tagId: Long) {
        tagDao.removeTagFromComic(ComicTagCrossRef(comicId = comicId, tagId = tagId))
    }
}
