package com.example.relisapp.data.local

import androidx.room.*
import com.example.relisapp.model.Lesson

@Dao
interface LessonDao {
    @Insert
    suspend fun insertLesson(lesson: Lesson)

    @Update
    suspend fun updateLesson(lesson: Lesson)

    @Delete
    suspend fun deleteLesson(lesson: Lesson)

    @Query("SELECT * FROM lessons WHERE id = :id LIMIT 1")
    suspend fun getLessonById(id: Int): Lesson?

    @Query("SELECT * FROM lessons")
    suspend fun getAllLessons(): List<Lesson>
}
