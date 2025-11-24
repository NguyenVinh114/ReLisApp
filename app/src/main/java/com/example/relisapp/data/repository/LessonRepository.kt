package com.example.relisapp.data.repository

import com.example.relisapp.data.local.dao.LessonDao
import com.example.relisapp.data.local.dao.ResultDao // <--- Đảm bảo Import dòng này
import com.example.relisapp.data.local.entity.Comments
import com.example.relisapp.data.local.entity.Lessons
import com.example.relisapp.data.local.entity.Results
import com.example.relisapp.data.local.entity.model.CommentWithUser
import com.example.relisapp.data.local.entity.model.QuestionWithChoices
import kotlinx.coroutines.flow.Flow

// 1. THÊM private val resultDao: ResultDao VÀO TRONG NGOẶC ĐƠN
class LessonRepository(
    private val lessonDao: LessonDao,
    private val resultDao: ResultDao
) {

    // --- CÁC HÀM LIÊN QUAN ĐẾN LESSON ---
    suspend fun getLessons(): List<Lessons> = lessonDao.getAll()
    suspend fun addLesson(lessons: Lessons) = lessonDao.insert(lessons)

    suspend fun getQuestionsWithChoicesForLesson(lessonId: Int): List<QuestionWithChoices> {
        return lessonDao.getQuestionsWithChoicesForLesson(lessonId)
    }

    suspend fun getLessonsByCategoryId(categoryId: Int): List<Lessons> {
        return lessonDao.getLessonsByCategoryId(categoryId)
    }

    suspend fun getLessonById(lessonId: Int): Lessons? {
        return lessonDao.getLessonById(lessonId)
    }

    // --- CÁC HÀM LIÊN QUAN ĐẾN COMMENT (Nếu bạn viết trong LessonDao) ---
    fun getCommentsForLesson(lessonId: Int): Flow<List<CommentWithUser>> {
        return lessonDao.getCommentsForLesson(lessonId)
    }

    suspend fun addComment(comment: Comments) {
        lessonDao.insertComment(comment)
    }

    // --- CÁC HÀM LIÊN QUAN ĐẾN RESULT ---
    suspend fun addResult(results: Results) {
        // 2. GỌI HÀM TỪ BIẾN resultDao ĐÃ KHAI BÁO Ở TRÊN
        resultDao.insertResult(results)
    }
}