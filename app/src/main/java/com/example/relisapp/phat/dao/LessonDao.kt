package com.example.relisapp.phat.dao

import androidx.room.*
import com.example.relisapp.phat.entity.Lessons
import com.example.relisapp.phat.entity.model.QuestionWithChoices

@Dao
interface LessonDao {
    @Query("SELECT * FROM Lessons")
    suspend fun getAll(): List<Lessons>

    @Insert
    suspend fun insert(lessons: Lessons)

    // Trong LessonDao.kt (hoặc QuestionDao.kt)
    @Transaction
    @Query("SELECT * FROM Questions WHERE lessonId = :lessonId")
    suspend fun getQuestionsWithChoicesForLesson(lessonId: Int): List<QuestionWithChoices>


    @Query("SELECT * FROM Lessons WHERE categoryId = :categoryId ORDER BY lessonId ASC")
    suspend fun getLessonsByCategoryId(categoryId: Int): List<Lessons>

    @Query("SELECT * FROM Lessons WHERE lessonId = :lessonId LIMIT 1")
    suspend fun getLessonById(lessonId: Int): Lessons?

}

