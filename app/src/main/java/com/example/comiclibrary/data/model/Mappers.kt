package com.example.comiclibrary.data.model

import com.example.comiclibrary.data.local.entity.CollectionEntity
import com.example.comiclibrary.data.local.entity.ComicEntity
import com.example.comiclibrary.data.local.entity.ComicWithCollections
import com.example.comiclibrary.data.local.entity.TagEntity
import com.example.comiclibrary.domain.model.Comic
import com.example.comiclibrary.domain.model.ComicCollection
import com.example.comiclibrary.domain.model.Tag

fun ComicWithCollections.toDomain(): Comic = Comic(
    id = comic.id,
    title = comic.title,
    folderUri = comic.folderUri,
    coverUri = comic.coverUri,
    pageCount = comic.pageCount,
    addedAtMillis = comic.addedAtMillis,
    lastReadAtMillis = comic.lastReadAtMillis,
    isFavorite = comic.isFavorite,
    collectionNames = collections.map { it.name },
    tags = tags.map { it.name }
)

fun ComicEntity.toDomain(collections: List<String> = emptyList(), tags: List<String> = emptyList()): Comic = Comic(
    id = id,
    title = title,
    folderUri = folderUri,
    coverUri = coverUri,
    pageCount = pageCount,
    addedAtMillis = addedAtMillis,
    lastReadAtMillis = lastReadAtMillis,
    isFavorite = isFavorite,
    collectionNames = collections,
    tags = tags
)

fun CollectionEntity.toDomain(comicCount: Int = 0): ComicCollection = ComicCollection(
    id = id,
    name = name,
    description = description,
    createdAtMillis = createdAtMillis,
    comicCount = comicCount
)

fun TagEntity.toDomain(): Tag = Tag(id = id, name = name)
