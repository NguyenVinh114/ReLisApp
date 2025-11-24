package com.example.relisapp.phat.dao

import androidx.room.*
import com.example.relisapp.phat.entity.FavoriteLessons

@Dao
interface FavoriteLessonDao {
    @Query("SELECT * FROM FavoriteLessons")
    suspend fun getAll(): List<FavoriteLessons>

    @Insert
    suspend fun insert(favoriteLessons: FavoriteLessons)
}