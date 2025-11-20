package com.example.relisapp.phat.repository

import com.example.relisapp.phat.dao.LessonDao
import com.example.relisapp.phat.entity.Lessons

class LessonRepository(private val lessonDao: LessonDao) {
    suspend fun getLessons(): List<Lessons> = lessonDao.getAll()
    suspend fun addLesson(lessons: Lessons) = lessonDao.insert(lessons)
}
