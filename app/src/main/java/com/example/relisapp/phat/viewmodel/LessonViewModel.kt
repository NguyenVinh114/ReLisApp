// In file: phat/viewmodel/LessonViewModel.kt

package com.example.relisapp.phat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.phat.entity.Lessons
import com.example.relisapp.phat.repository.LessonRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



class LessonViewModel(private val repository: LessonRepository) : ViewModel() {


    private val _allLessons = MutableStateFlow<List<Lessons>>(emptyList())

    private val _lessons = MutableStateFlow<List<Lessons>>(emptyList())
    val lessons: StateFlow<List<Lessons>> = _lessons.asStateFlow()

    private val _lessonDetails = MutableStateFlow<Lessons?>(null)
    val lessonDetails: StateFlow<Lessons?> = _lessonDetails.asStateFlow()



    fun loadAllLessons() {
        viewModelScope.launch {
            repository.getAllLessons().collect { lessonList ->
                _allLessons.value = lessonList
                _lessons.value = lessonList // Cập nhật cả danh sách hiển thị
            }
        }
    }


    fun loadLessonDetails(lessonId: Int) {
        viewModelScope.launch {
            repository.getLessonById(lessonId).collect { lesson ->
                _lessonDetails.value = lesson
            }
        }
    }


    fun searchLessons(query: String) {
        val filteredList = if (query.isBlank()) {
            _allLessons.value // Nếu query rỗng, trả về danh sách gốc
        } else {
            _allLessons.value.filter {
                it.title.contains(query, ignoreCase = true)
            }
        }
        _lessons.value = filteredList
    }

    fun filterLessons(type: String?, categoryId: Int?) {
        var filteredList = _allLessons.value

        if (type != null) {
            filteredList = filteredList.filter { it.type == type }
        }

        if (categoryId != null) {
            filteredList = filteredList.filter { it.categoryId == categoryId }
        }
        _lessons.value = filteredList
    }

    private val _saveResult = MutableSharedFlow<SaveResult>(replay = 0)
    val saveResult = _saveResult.asSharedFlow()

    fun addLesson(lesson: Lessons) {
        viewModelScope.launch {
            try {
                val exists = repository.doesLessonExist(lesson.title.trim(), lesson.categoryId)
                if (!exists) {
                    repository.addLesson(lesson)
                    _saveResult.emit(SaveResult.Success) // [SỬA ĐỔI 2] Dùng emit()
                } else {
                    _saveResult.emit(SaveResult.Existed("A lesson with this title already exists in this category."))
                }
            } catch (e: Exception) {
                _saveResult.emit(SaveResult.Failure(e))
            }
        }
    }

    fun updateLesson(lesson: Lessons) {
        viewModelScope.launch {
            try {
                val exists = repository.doesLessonExist(lesson.title.trim(), lesson.categoryId, lesson.lessonId)
                if (!exists) {
                    repository.updateLesson(lesson)
                    _saveResult.emit(SaveResult.Success) // [SỬA ĐỔI 3] Dùng emit()
                } else {
                    _saveResult.emit(SaveResult.Existed("Another lesson with this title already exists in this category."))
                }
            } catch (e: Exception) {
                _saveResult.emit(SaveResult.Failure(e))
            }
        }
    }

    fun lockLesson(lesson: Lessons) {
        viewModelScope.launch {
            val lockedLesson = lesson.copy(isLocked = 1)
            repository.updateLesson(lockedLesson)
        }
    }

    fun unlockLesson(lesson: Lessons) {
        viewModelScope.launch {
            val unlockedLesson = lesson.copy(isLocked = 0)
            repository.updateLesson(unlockedLesson)
        }
    }

    fun loadLessonsForCategory(categoryId: Int) {
        viewModelScope.launch {
            repository.getLessonsByCategoryId(categoryId).collect { lessonList ->
                // Không cần _allLessons ở đây vì ta chỉ quan tâm đến danh sách đã lọc
                _lessons.value = lessonList
            }
        }
    }
}
