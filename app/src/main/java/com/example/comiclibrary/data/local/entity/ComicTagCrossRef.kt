package com.example.comiclibrary.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "comic_tag_cross_ref",
    primaryKeys = ["comicId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = ComicEntity::class,
            parentColumns = ["id"],
            childColumns = ["comicId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("comicId"), Index("tagId")]
)
data class ComicTagCrossRef(
    @ColumnInfo(name = "comicId")
    val comicId: Long,

    @ColumnInfo(name = "tagId")
    val tagId: Long
)
