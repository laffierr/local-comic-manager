package com.example.comiclibrary.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "comics",
    indices = [Index(value = ["folderUri"], unique = true)]
)
data class ComicEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "folderUri")
    val folderUri: String,

    @ColumnInfo(name = "coverUri")
    val coverUri: String?,

    @ColumnInfo(name = "pageCount")
    val pageCount: Int = 0,

    @ColumnInfo(name = "addedAtMillis")
    val addedAtMillis: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "lastReadAtMillis")
    val lastReadAtMillis: Long? = null,

    @ColumnInfo(name = "isFavorite")
    val isFavorite: Boolean = false
)
