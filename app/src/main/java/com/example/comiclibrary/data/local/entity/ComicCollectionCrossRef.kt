package com.example.comiclibrary.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "comic_collection_cross_ref",
    primaryKeys = ["comicId", "collectionId"],
    foreignKeys = [
        ForeignKey(
            entity = ComicEntity::class,
            parentColumns = ["id"],
            childColumns = ["comicId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("comicId"),
        Index("collectionId")
    ]
)
data class ComicCollectionCrossRef(
    @ColumnInfo(name = "comicId")
    val comicId: Long,

    @ColumnInfo(name = "collectionId")
    val collectionId: Long
)
