package com.example.comiclibrary.di

import android.content.Context
import androidx.room.Room
import com.example.comiclibrary.data.local.AppDatabase
import com.example.comiclibrary.data.local.dao.CollectionDao
import com.example.comiclibrary.data.local.dao.ComicCollectionDao
import com.example.comiclibrary.data.local.dao.ComicDao
import com.example.comiclibrary.data.local.dao.TagDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "comic_library.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideComicDao(db: AppDatabase): ComicDao = db.comicDao()
    @Provides
    fun provideCollectionDao(db: AppDatabase): CollectionDao = db.collectionDao()
    @Provides
    fun provideComicCollectionDao(db: AppDatabase): ComicCollectionDao = db.comicCollectionDao()
    @Provides
    fun provideTagDao(db: AppDatabase): TagDao = db.tagDao()
}
