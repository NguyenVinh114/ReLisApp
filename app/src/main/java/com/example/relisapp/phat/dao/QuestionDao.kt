package com.example.relisapp.phat.dao

import androidx.room.*
import com.example.relisapp.phat.entity.Questions

@Dao
interface QuestionDao {
    @Query("SELECT * FROM Questions")
    suspend fun getAll(): List<Questions>

    @Insert
    suspend fun insert(questions: Questions)
}
