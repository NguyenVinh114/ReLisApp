package com.example.relisapp.nam.database.dao

import androidx.room.*
import com.example.relisapp.nam.database.entity.Likes

@Dao
interface LikeDao {
    @Query("SELECT * FROM Likes")
    suspend fun getAll(): List<Likes>

    @Insert
    suspend fun insert(likes: Likes)

    @Delete
    suspend fun delete(like: Likes)
}
