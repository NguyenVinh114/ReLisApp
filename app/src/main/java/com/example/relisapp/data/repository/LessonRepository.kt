package com.example.relisapp.data.repository

import com.example.relisapp.data.local.LessonDao
import com.example.relisapp.model.Lesson

class LessonRepository(private val lessonDao: LessonDao) {

    suspend fun insertLesson(lesson: Lesson) = lessonDao.insertLesson(lesson)

    suspend fun updateLesson(lesson: Lesson) = lessonDao.updateLesson(lesson)

    suspend fun deleteLesson(lesson: Lesson) = lessonDao.deleteLesson(lesson)

    suspend fun getLessonById(id: Int) = lessonDao.getLessonById(id)

    suspend fun getAllLessons() = lessonDao.getAllLessons()
}
