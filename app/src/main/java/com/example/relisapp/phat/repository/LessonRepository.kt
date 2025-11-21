package com.example.relisapp.phat.repository

import com.example.relisapp.phat.dao.LessonDao
import com.example.relisapp.phat.entity.Lessons
import com.example.relisapp.phat.entity.model.QuestionWithChoices

class LessonRepository(private val lessonDao: LessonDao) {
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

}
