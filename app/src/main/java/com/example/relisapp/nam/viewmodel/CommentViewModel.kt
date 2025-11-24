package com.example.relisapp.nam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.nam.database.entity.Comments
import com.example.relisapp.nam.data.repository.CommentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CommentViewModel(private val repo: CommentRepository) : ViewModel() {

    private val _comments = MutableStateFlow<List<Comments>>(emptyList())
    val comments: StateFlow<List<Comments>> = _comments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadAllComments()
    }

    // ✅ Đã sửa: Không dùng collect, gán trực tiếp giá trị
    fun loadAllComments() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                // Vì repo trả về List<Comments>, ta gán thẳng vào StateFlow
                val list = repo.getAllComments()
                _comments.value = list
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ✅ Đã sửa: Không dùng collect
    fun loadCommentsByLesson(lessonId: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                // Lưu ý: Cần đảm bảo Repo/Dao đã có hàm getCommentsByLessonId(lessonId)
                val list = repo.getCommentsByLessonId(lessonId)
                _comments.value = list
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Hàm load thủ công (giống loadAllComments, giữ lại nếu cần tương thích ngược)
    fun loadComments() {
        loadAllComments()
    }

    fun addComment(comment: Comments, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                // 1. Thêm vào DB
                repo.addComment(comment)

                // 2. Reload lại danh sách (QUAN TRỌNG: vì không dùng Flow tự động update)
                // Kiểm tra xem đang filter theo lesson hay lấy tất cả để reload cho đúng
                if (comment.lessonId > 0) {
                    loadCommentsByLesson(comment.lessonId)
                } else {
                    loadAllComments()
                }

                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message
                onError(e.message ?: "Lỗi không xác định")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteComment(comment: Comments, onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                // 1. Xóa khỏi DB
                repo.deleteComment(comment)

                // 2. Reload lại danh sách để cập nhật UI
                // Cách đơn giản nhất là lọc list hiện tại để UI update ngay lập tức
                val currentList = _comments.value.toMutableList()
                currentList.remove(comment)
                _comments.value = currentList

                // Hoặc gọi load lại từ DB để chắc chắn: loadAllComments()

                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message
                onError(e.message ?: "Lỗi không xác định")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}