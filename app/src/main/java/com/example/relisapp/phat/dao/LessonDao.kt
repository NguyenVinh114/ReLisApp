// Trong file .../phat/dao/LessonDao.kt

package com.example.relisapp.phat.dao

import androidx.room.*
import com.example.relisapp.phat.entity.Lessons
import com.example.relisapp.phat.entity.model.QuestionWithChoices
import kotlinx.coroutines.flow.Flow // <-- THÊM IMPORT NÀY

@Dao
interface LessonDao {
    @Query("SELECT * FROM Lessons ORDER BY lessonId DESC") // Thêm ORDER BY để danh sách luôn nhất quán
    fun getAll(): Flow<List<Lessons>>

    @Insert
    suspend fun insert(lessons: Lessons)

    @Transaction
    @Query("SELECT * FROM Questions WHERE lessonId = :lessonId")
    fun getQuestionsWithChoicesForLesson(lessonId: Int): Flow<List<QuestionWithChoices>>

    @Query("SELECT * FROM Lessons WHERE categoryId = :categoryId and isLocked = 0 ORDER BY lessonId ASC")
    fun getLessonsByCategoryId(categoryId: Int): Flow<List<Lessons>>

    @Query("SELECT * FROM Lessons WHERE lessonId = :lessonId LIMIT 1")
    fun getLessonById(lessonId: Int): Flow<Lessons?>

    @Update
    suspend fun updateLesson(lesson: Lessons)

    @Delete
    suspend fun deleteLesson(lesson: Lessons)

    @Query("SELECT EXISTS(SELECT 1 FROM Lessons WHERE title = :title AND categoryId = :categoryId LIMIT 1)")
    suspend fun lessonExists(title: String, categoryId: Int): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM Lessons WHERE title = :title AND categoryId = :categoryId AND lessonId != :excludeLessonId LIMIT 1)")
    suspend fun lessonExistsExcludingId(title: String, categoryId: Int, excludeLessonId: Int): Boolean

    @Query("SELECT * FROM Lessons WHERE isLocked = 0 ORDER BY lessonId DESC")
    fun getAllForUser(): Flow<List<Lessons>>

    @Query("SELECT * FROM Lessons WHERE categoryId = :categoryId AND isLocked = 0 ORDER BY lessonId ASC")
    fun getLessonsByCategoryIdForUser(categoryId: Int): Flow<List<Lessons>>

    @Query("SELECT * FROM Lessons WHERE lessonId = :lessonId AND isLocked = 0 LIMIT 1")
    fun getLessonByIdForUser(lessonId: Int): Flow<Lessons?>

}
