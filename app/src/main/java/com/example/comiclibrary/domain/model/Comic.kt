package com.example.comiclibrary.domain.model

data class Comic(
    val id: Long,
    val title: String,
    val folderUri: String,
    val coverUri: String?,
    val pageCount: Int,
    val addedAtMillis: Long,
    val lastReadAtMillis: Long?,
    val isFavorite: Boolean = false,
    val collectionNames: List<String> = emptyList(),
    val tags: List<String> = emptyList()
)
