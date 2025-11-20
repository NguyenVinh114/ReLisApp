package com.example.relisapp.phat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.phat.entity.Lessons
import com.example.relisapp.phat.repository.LessonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LessonViewModel(private val repo: LessonRepository) : ViewModel() {

    private val _lessons = MutableStateFlow<List<Lessons>>(emptyList())
    val lessons: StateFlow<List<Lessons>> = _lessons

    fun loadLessons() {
        viewModelScope.launch {
            _lessons.value = repo.getLessons()
        }
    }

    fun addLesson(lessons: Lessons) {
        viewModelScope.launch {
            repo.addLesson(lessons)
            loadLessons()
        }
    }
}
