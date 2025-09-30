package com.example.relisapp.ui.favorite

import androidx.compose.runtime.mutableStateListOf

// ✅ Quản lý danh sách Favorite toàn cục
object FavoriteManager {
    val favorites = mutableStateListOf<String>()

    fun addFavorite(lesson: String) {
        if (!favorites.contains(lesson)) {
            favorites.add(lesson)
        }
    }

    fun removeFavorite(lesson: String) {
        favorites.remove(lesson)
    }

    fun clear() {
        favorites.clear()
    }
}