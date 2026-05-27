package com.example.comiclibrary.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.comiclibrary.data.local.dao.CollectionDao
import com.example.comiclibrary.data.local.dao.ComicCollectionDao
import com.example.comiclibrary.data.local.dao.ComicDao
import com.example.comiclibrary.data.local.dao.TagDao
import com.example.comiclibrary.data.local.entity.CollectionEntity
import com.example.comiclibrary.data.local.entity.ComicCollectionCrossRef
import com.example.comiclibrary.data.local.entity.ComicEntity
import com.example.comiclibrary.data.local.entity.ComicTagCrossRef
import com.example.comiclibrary.data.local.entity.TagEntity

@Database(
    entities = [
        ComicEntity::class,
        CollectionEntity::class,
        ComicCollectionCrossRef::class,
        TagEntity::class,
        ComicTagCrossRef::class
    ],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun comicDao(): ComicDao
    abstract fun collectionDao(): CollectionDao
    abstract fun comicCollectionDao(): ComicCollectionDao
    abstract fun tagDao(): TagDao
}
