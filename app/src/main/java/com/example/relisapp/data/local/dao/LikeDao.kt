package com.example.relisapp.data.local.dao

import androidx.room.*
import com.example.relisapp.data.local.entity.Likes

@Dao
interface LikeDao {
    @Query("SELECT * FROM Likes")
    suspend fun getAll(): List<Likes>

    @Insert
    suspend fun insert(likes: Likes)
}
