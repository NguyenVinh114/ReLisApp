package com.example.relisapp.nam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.nam.database.entity.Lessons
import com.example.relisapp.nam.data.repository.LessonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LessonViewModel(private val repo: LessonRepository) : ViewModel() {

    private val _lessons = MutableStateFlow<List<Lessons>>(emptyList())
    val lessons: StateFlow<List<Lessons>> = _lessons.asStateFlow()

    private val _currentLesson = MutableStateFlow<Lessons?>(null)
    val currentLesson: StateFlow<Lessons?> = _currentLesson.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadAllLessons()
    }

    fun loadAllLessons() {
        viewModelScope.launch {
            execute { _lessons.value = repo.getAllLessons() }
        }
    }

    fun loadLessonsByCategory(categoryId: Int) {
        viewModelScope.launch {
            execute { _lessons.value = repo.getLessonsByCategory(categoryId) }
        }
    }

    fun loadLessonsByType(type: String) {
        viewModelScope.launch {
            execute { _lessons.value = repo.getLessonsByType(type) }
        }
    }

    fun loadLessonsByLevel(level: String) {
        viewModelScope.launch {
            execute { _lessons.value = repo.getLessonsByLevel(level) }
        }
    }

    fun searchLessons(query: String) {
        viewModelScope.launch {
            execute {
                // Thêm dấu % để tìm kiếm gần đúng
                _lessons.value = repo.searchLessons("%$query%")
            }
        }
    }

    fun loadLessonById(lessonId: Int) {
        viewModelScope.launch {
            execute { _currentLesson.value = repo.getLessonById(lessonId) }
        }
    }

    fun addLesson(lesson: Lessons, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repo.insertLesson(lesson)
                loadAllLessons() // Refresh list
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message
                onError(e.message ?: "Unknown error")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateLesson(lesson: Lessons, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repo.updateLesson(lesson)
                loadAllLessons() // Refresh list
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message
                onError(e.message ?: "Unknown error")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteLesson(lesson: Lessons, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repo.deleteLesson(lesson)
                // Cập nhật nhanh list UI
                val currentList = _lessons.value.toMutableList()
                currentList.remove(lesson)
                _lessons.value = currentList
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message
                onError(e.message ?: "Unknown error")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    // Hàm helper để giảm trùng lặp code try-catch
    private suspend fun execute(block: suspend () -> Unit) {
        try {
            _isLoading.value = true
            block()
        } catch (e: Exception) {
            _error.value = e.message
        } finally {
            _isLoading.value = false
        }
    }
}