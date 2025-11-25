package com.example.relisapp.data.repository

import com.example.relisapp.data.local.dao.FavoriteLessonDao
import com.example.relisapp.data.local.entity.FavoriteLessons

class FavoriteLessonRepository(private val favoriteLessonDao: FavoriteLessonDao) {
    suspend fun getFavorites(): List<FavoriteLessons> = favoriteLessonDao.getAll()
    suspend fun addFavorite(fav: FavoriteLessons) = favoriteLessonDao.insert(fav)
}
