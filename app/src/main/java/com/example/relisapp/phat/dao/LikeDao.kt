package com.example.relisapp.phat.dao

import androidx.room.*
import com.example.relisapp.phat.entity.Likes

@Dao
interface LikeDao {
    @Query("SELECT * FROM Likes")
    suspend fun getAll(): List<Likes>

    @Insert
    suspend fun insert(likes: Likes)
}
