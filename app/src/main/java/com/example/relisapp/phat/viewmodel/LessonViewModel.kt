// In file: phat/viewmodel/LessonViewModel.kt

package com.example.relisapp.phat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.phat.entity.Lessons
import com.example.relisapp.phat.repository.LessonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel này CHỈ chịu trách nhiệm cho các nghiệp vụ liên quan đến LESSON.
 */
class LessonViewModel(private val repository: LessonRepository) : ViewModel() {

    // --- STATE CHO MÀN HÌNH DANH SÁCH BÀI HỌC ---

    // _allLessons là nguồn dữ liệu gốc, không thay đổi khi lọc/tìm kiếm.
    private val _allLessons = MutableStateFlow<List<Lessons>>(emptyList())

    // _lessons là danh sách được hiển thị trên UI, sẽ thay đổi khi lọc/tìm kiếm.
    private val _lessons = MutableStateFlow<List<Lessons>>(emptyList())
    val lessons: StateFlow<List<Lessons>> = _lessons.asStateFlow()

    // --- STATE CHO MÀN HÌNH CHI TIẾT BÀI HỌC ---
    private val _lessonDetails = MutableStateFlow<Lessons?>(null)
    val lessonDetails: StateFlow<Lessons?> = _lessonDetails.asStateFlow()


    // --- CÁC HÀM XỬ LÝ LOGIC CHO LESSON ---

    /**
     * Tải toàn bộ danh sách bài học từ repository.
     */
    fun loadAllLessons() {
        viewModelScope.launch {
            repository.getAllLessons().collect { lessonList ->
                _allLessons.value = lessonList
                _lessons.value = lessonList // Cập nhật cả danh sách hiển thị
            }
        }
    }

    /**
     * Tải thông tin chi tiết của MỘT bài học.
     * Dùng cho màn hình QuestionListScreen để lấy content, hoặc màn hình sửa bài học.
     */
    fun loadLessonDetails(lessonId: Int) {
        viewModelScope.launch {
            repository.getLessonById(lessonId).collect { lesson ->
                _lessonDetails.value = lesson
            }
        }
    }

    /**
     * Tìm kiếm bài học dựa trên tiêu đề.
     */
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

    /**
     * Lọc danh sách bài học dựa trên loại (type) và/hoặc danh mục (category).
     */
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

    /**
     * Thêm một bài học mới vào cơ sở dữ liệu.
     */
    fun addLesson(lesson: Lessons) {
        viewModelScope.launch {
            repository.addLesson(lesson)
        }
    }

    /**
     * Cập nhật một bài học đã có.
     */
    fun updateLesson(lesson: Lessons) {
        viewModelScope.launch {
            repository.updateLesson(lesson)
        }
    }

    /**
     * Xóa một bài học.
     */
    fun deleteLesson(lesson: Lessons) {
        viewModelScope.launch {
            repository.deleteLesson(lesson)
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
