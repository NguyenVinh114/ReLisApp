package com.example.relisapp.phat.dao

import androidx.room.*
import com.example.relisapp.phat.entity.Users

@Dao
interface UserDao {
    @Query("SELECT * FROM Users")
    suspend fun getAll(): List<Users>

    @Insert
    suspend fun insert(users: Users)
}