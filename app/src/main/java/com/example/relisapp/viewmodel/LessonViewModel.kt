package com.example.relisapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.data.local.AppDatabase
import com.example.relisapp.data.repository.LessonRepository
import com.example.relisapp.model.Lesson
import kotlinx.coroutines.launch

class LessonViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LessonRepository

    init {
        val lessonDao = AppDatabase.getDatabase(application).lessonDao()
        repository = LessonRepository(lessonDao)
    }

    fun insertLesson(lesson: Lesson) = viewModelScope.launch {
        repository.insertLesson(lesson)
    }

    fun updateLesson(lesson: Lesson) = viewModelScope.launch {
        repository.updateLesson(lesson)
    }

    fun deleteLesson(lesson: Lesson) = viewModelScope.launch {
        repository.deleteLesson(lesson)
    }

    suspend fun getLessonById(id: Int) = repository.getLessonById(id)

    suspend fun getAllLessons() = repository.getAllLessons()
}
