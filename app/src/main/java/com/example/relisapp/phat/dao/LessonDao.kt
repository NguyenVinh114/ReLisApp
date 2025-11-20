package com.example.relisapp.phat.dao

import androidx.room.*
import com.example.relisapp.phat.entity.Lessons

@Dao
interface LessonDao {
    @Query("SELECT * FROM Lessons")
    suspend fun getAll(): List<Lessons>

    @Insert
    suspend fun insert(lessons: Lessons)
}