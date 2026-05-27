package com.example.comiclibrary.di

import com.example.comiclibrary.data.repository.CollectionRepository
import com.example.comiclibrary.data.repository.CollectionRepositoryImpl
import com.example.comiclibrary.data.repository.ComicRepository
import com.example.comiclibrary.data.repository.ComicRepositoryImpl
import com.example.comiclibrary.data.repository.TagRepository
import com.example.comiclibrary.data.repository.TagRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindComicRepository(impl: ComicRepositoryImpl): ComicRepository

    @Binds
    @Singleton
    abstract fun bindCollectionRepository(impl: CollectionRepositoryImpl): CollectionRepository

    @Binds
    @Singleton
    abstract fun bindTagRepository(impl: TagRepositoryImpl): TagRepository
}
