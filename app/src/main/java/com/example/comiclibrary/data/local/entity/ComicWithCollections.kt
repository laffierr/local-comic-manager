package com.example.comiclibrary.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ComicWithCollections(
    @Embedded val comic: ComicEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ComicCollectionCrossRef::class,
            parentColumn = "comicId",
            entityColumn = "collectionId"
        )
    )
    val collections: List<CollectionEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ComicTagCrossRef::class,
            parentColumn = "comicId",
            entityColumn = "tagId"
        )
    )
    val tags: List<TagEntity>
)
