package com.example.relisapp.phat.dao

import androidx.room.*
import com.example.relisapp.phat.entity.Comments

@Dao
interface CommentDao {
    @Query("SELECT * FROM Comments")
    suspend fun getAll(): List<Comments>

    @Insert
    suspend fun insert(comments: Comments)
}