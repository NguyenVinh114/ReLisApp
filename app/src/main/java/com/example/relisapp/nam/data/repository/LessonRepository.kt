package com.example.relisapp.nam.data.repository

import com.example.relisapp.nam.database.dao.LessonDao
import com.example.relisapp.nam.database.entity.Lessons

class LessonRepository(private val lessonDao: LessonDao) {

    suspend fun getAllLessons() = lessonDao.getAll()

    suspend fun getLessonById(id: Int) = lessonDao.getLessonById(id)

    suspend fun getLessonsByCategory(id: Int) = lessonDao.getByCategory(id)

    suspend fun getLessonsByType(type: String) = lessonDao.getByType(type)

    suspend fun getLessonsByLevel(level: String) = lessonDao.getByLevel(level)

    suspend fun searchLessons(query: String) = lessonDao.search(query)

    // Insert trả về Long (ID của row vừa tạo) để ViewModel kiểm tra
    suspend fun insertLesson(lesson: Lessons): Long = lessonDao.insert(lesson)

    suspend fun updateLesson(lesson: Lessons) = lessonDao.update(lesson)

    suspend fun deleteLesson(lesson: Lessons) = lessonDao.delete(lesson)
}