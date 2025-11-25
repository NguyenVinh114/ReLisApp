package com.example.relisapp.data.local.dao

import androidx.room.*
import com.example.relisapp.data.local.entity.Categories

@Dao
interface CategoryDao {
    @Query("SELECT * FROM Categories")
    suspend fun getAll(): List<Categories>

    @Insert
    suspend fun insert(categories: Categories)
}