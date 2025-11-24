package com.example.relisapp.phat.repository

import com.example.relisapp.phat.dao.FavoriteLessonDao
import com.example.relisapp.phat.entity.FavoriteLessons

class FavoriteLessonRepository(private val favoriteLessonDao: FavoriteLessonDao) {
    suspend fun getFavorites(): List<FavoriteLessons> = favoriteLessonDao.getAll()
    suspend fun addFavorite(fav: FavoriteLessons) = favoriteLessonDao.insert(fav)
}
