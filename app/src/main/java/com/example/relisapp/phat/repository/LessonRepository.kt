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

    // --- HÀM QUAN TRỌNG ĐỂ LẤY DỮ LIỆU QUIZ ---
    // Hàm này lấy các câu hỏi và lựa chọn từ bảng Questions và Choices
    fun getQuestionsWithChoicesForLesson(lessonId: Int): Flow<List<QuestionWithChoices>> {
        return lessonDao.getQuestionsWithChoicesForLesson(lessonId)
    }


}
