package com.example.comiclibrary.domain.model

data class ComicCollection(
    val id: Long,
    val name: String,
    val description: String,
    val createdAtMillis: Long,
    val comicCount: Int = 0
)
