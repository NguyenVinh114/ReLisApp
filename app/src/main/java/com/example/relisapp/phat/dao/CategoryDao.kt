package com.example.relisapp.phat.dao

import androidx.room.*
import com.example.relisapp.phat.entity.Categories

@Dao
interface CategoryDao {
    @Query("SELECT * FROM Categories")
    suspend fun getAll(): List<Categories>

    @Insert
    suspend fun insert(categories: Categories)
}