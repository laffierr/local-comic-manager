package com.example.comiclibrary.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Bookshelf : Screen("bookshelf")
    object Favorites : Screen("favorites")

    object CollectionDetail : Screen("favorites/collection/{collectionId}") {
        fun createRoute(collectionId: Long) = "favorites/collection/$collectionId"
    }

    object ComicDetail : Screen("comic/{comicId}") {
        fun createRoute(comicId: Long) = "comic/$comicId"
    }

    object Reader : Screen("reader/{comicId}") {
        fun createRoute(comicId: Long) = "reader/$comicId"
    }

    object Tags : Screen("tags")

    object TagComics : Screen("tag-comics/{tagId}/{tagName}") {
        fun createRoute(tagId: Long?, tagName: String): String {
            val encoded = java.net.URLEncoder.encode(tagName, "UTF-8")
            return "tag-comics/${tagId ?: -1}/$encoded"
        }
    }
}
