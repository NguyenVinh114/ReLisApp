// File: com/example/relisapp/phat/repository/LessonRepository.kt
package com.example.relisapp.phat.repository

import com.example.relisapp.phat.dao.LessonDao
import com.example.relisapp.phat.entity.Lessons
import com.example.relisapp.phat.entity.model.QuestionWithChoices
import kotlinx.coroutines.flow.Flow

class LessonRepository(private val lessonDao: LessonDao) {

    // --- CÁC HÀM TƯƠNG TÁC VỚI BẢNG LESSONS ---

    fun getAllLessons(): Flow<List<Lessons>> {
        return lessonDao.getAll()
    }

    fun getLessonsByCategoryId(categoryId: Int): Flow<List<Lessons>> {
        return lessonDao.getLessonsByCategoryId(categoryId)
    }

    fun getLessonById(lessonId: Int): Flow<Lessons?> {
        return lessonDao.getLessonById(lessonId)
    }

    // [THÊM MỚI] Hàm để thêm Lesson, gọi đến DAO
    suspend fun addLesson(lessons: Lessons) {
        lessonDao.insert(lessons)
    }

    suspend fun updateLesson(lesson: Lessons) {
        lessonDao.updateLesson(lesson)
    }

    suspend fun deleteLesson(lesson: Lessons) {
        lessonDao.deleteLesson(lesson)
    }

    suspend fun doesLessonExist(title: String, categoryId: Int): Boolean {
        return lessonDao.lessonExists(title, categoryId)
    }

    suspend fun doesLessonExist(title: String, categoryId: Int, excludeLessonId: Int): Boolean {
        return lessonDao.lessonExistsExcludingId(title, categoryId, excludeLessonId)
    }

    fun getAllLessonsForUser(): Flow<List<Lessons>> {
        return lessonDao.getAllForUser()
    }

    fun getLessonsByCategoryIdForUser(categoryId: Int): Flow<List<Lessons>> {
        return lessonDao.getLessonsByCategoryIdForUser(categoryId)
    }

    fun getLessonByIdForUser(lessonId: Int): Flow<Lessons?> {
        return lessonDao.getLessonByIdForUser(lessonId)
    }

}
