package com.example.relisapp.data.repository

import com.example.relisapp.data.local.FavoriteDao
import com.example.relisapp.model.Favorite

class FavoriteRepository(private val favoriteDao: FavoriteDao) {

    suspend fun insertFavorite(favorite: Favorite) = favoriteDao.insertFavorite(favorite)

    suspend fun deleteFavorite(favorite: Favorite) = favoriteDao.deleteFavorite(favorite)

    suspend fun getFavoritesByUser(userId: Int) = favoriteDao.getFavoritesByUser(userId)

    suspend fun getFavorite(userId: Int, lessonId: Int) = favoriteDao.getFavorite(userId, lessonId)
}
