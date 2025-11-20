package com.example.relisapp.phat.dao

import androidx.room.*
import com.example.relisapp.phat.entity.Results
@Dao
interface ResultDao {
    @Query("SELECT * FROM Results")
    suspend fun getAll(): List<Results>

    @Insert
    suspend fun insert(results: Results)
}