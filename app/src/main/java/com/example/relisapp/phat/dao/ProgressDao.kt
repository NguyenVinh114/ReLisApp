package com.example.relisapp.phat.dao

import androidx.room.*
import com.example.relisapp.phat.entity.Progress

@Dao
interface ProgressDao {
    @Query("SELECT * FROM Progress")
    suspend fun getAll(): List<Progress>

    @Insert
    suspend fun insert(progress: Progress)
}