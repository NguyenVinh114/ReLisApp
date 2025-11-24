// Trong file .../phat/dao/LessonDao.kt

package com.example.relisapp.phat.dao

import androidx.room.*
import com.example.relisapp.phat.entity.Lessons
import com.example.relisapp.phat.entity.model.QuestionWithChoices
import kotlinx.coroutines.flow.Flow // <-- THÊM IMPORT NÀY

@Dao
interface LessonDao {
    @Query("SELECT * FROM Lessons ORDER BY lessonId DESC") // Thêm ORDER BY để danh sách luôn nhất quán
    fun getAll(): Flow<List<Lessons>> // SỬA 1: Bỏ suspend, trả về Flow

    @Insert
    suspend fun insert(lessons: Lessons)

    @Transaction
    @Query("SELECT * FROM Questions WHERE lessonId = :lessonId")
    fun getQuestionsWithChoicesForLesson(lessonId: Int): Flow<List<QuestionWithChoices>> // SỬA 2: Bỏ suspend, trả về Flow

    @Query("SELECT * FROM Lessons WHERE categoryId = :categoryId ORDER BY lessonId ASC")
    fun getLessonsByCategoryId(categoryId: Int): Flow<List<Lessons>> // SỬA 3: Bỏ suspend, trả về Flow

    @Query("SELECT * FROM Lessons WHERE lessonId = :lessonId LIMIT 1")
    fun getLessonById(lessonId: Int): Flow<Lessons?> // SỬA 4: Bỏ suspend, trả về Flow

    @Update
    suspend fun updateLesson(lesson: Lessons)

    @Delete
    suspend fun deleteLesson(lesson: Lessons)
}
