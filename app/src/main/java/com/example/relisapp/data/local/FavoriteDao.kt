package com.example.relisapp.data.local

import androidx.room.*
import com.example.relisapp.model.Favorite

@Dao
interface FavoriteDao {
    @Insert
    suspend fun insertFavorite(favorite: Favorite)

    @Delete
    suspend fun deleteFavorite(favorite: Favorite)

    @Query("SELECT * FROM favorites WHERE userId = :userId")
    suspend fun getFavoritesByUser(userId: Int): List<Favorite>

    @Query("SELECT * FROM favorites WHERE userId = :userId AND lessonId = :lessonId LIMIT 1")
    suspend fun getFavorite(userId: Int, lessonId: Int): Favorite?
}
