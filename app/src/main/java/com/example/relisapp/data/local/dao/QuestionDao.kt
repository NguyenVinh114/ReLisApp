package com.example.relisapp.data.local.dao

import androidx.room.*
import com.example.relisapp.data.local.entity.Questions

@Dao
interface QuestionDao {
    @Query("SELECT * FROM Questions")
    suspend fun getAll(): List<Questions>

    @Insert
    suspend fun insert(questions: Questions)
}
