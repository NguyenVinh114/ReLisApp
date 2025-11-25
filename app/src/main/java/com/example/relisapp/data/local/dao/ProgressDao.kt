package com.example.relisapp.data.local.dao

import androidx.room.*
import com.example.relisapp.data.local.entity.Progress

@Dao
interface ProgressDao {
    @Query("SELECT * FROM Progress")
    suspend fun getAll(): List<Progress>

    @Insert
    suspend fun insert(progress: Progress)
}