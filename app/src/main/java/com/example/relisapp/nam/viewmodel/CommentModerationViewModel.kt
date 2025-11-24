package com.example.relisapp.nam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.nam.data.repository.CommentRepository
import com.example.relisapp.nam.data.repository.LessonRepository
import com.example.relisapp.nam.data.repository.UserRepository
import com.example.relisapp.nam.database.entity.Comments
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Dữ liệu hiển thị 1 dòng bình luận kèm tên user & bài học
data class CommentWithDetails(
    val comment: Comments,
    val userName: String,
    val lessonTitle: String
)

// UiState cho màn Moderation
sealed class ModerationUiState {
    object Loading : ModerationUiState()
    data class Success(val comments: List<CommentWithDetails>) : ModerationUiState()
    data class Error(val message: String) : ModerationUiState()
}

class CommentModerationViewModel(
    private val commentRepo: CommentRepository,
    private val userRepo: UserRepository,
    private val lessonRepo: LessonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ModerationUiState>(ModerationUiState.Loading)
    val uiState: StateFlow<ModerationUiState> = _uiState

    init {
        loadComments()
    }

    // Load danh sách comment + lấy thêm tên User và tên Bài học
    fun loadComments() {
        viewModelScope.launch {
            try {
                _uiState.value = ModerationUiState.Loading

                val comments = commentRepo.getAllComments()

                // Map dữ liệu để lấy tên thay vì ID
                val enhanced = comments.map { c ->
                    CommentWithDetails(
                        comment = c,
                        userName = loadUserName(c.userId),
                        lessonTitle = loadLessonTitle(c.lessonId)
                    )
                }

                _uiState.value = ModerationUiState.Success(enhanced)

            } catch (e: Exception) {
                _uiState.value = ModerationUiState.Error(e.message ?: "Lỗi tải bình luận")
            }
        }
    }

    private suspend fun loadUserName(id: Int): String {
        // Giả định UserRepository có hàm getUserById trả về nullable User
        val user = userRepo.getUserById(id)
        // Fallback nếu user null hoặc chưa có field fullName
        return user?.fullName ?: user?.username ?: "User #$id"
    }

    private suspend fun loadLessonTitle(id: Int): String {
        // Giả định LessonRepository có hàm getLessonById
        val lesson = lessonRepo.getLessonById(id)
        return lesson?.title ?: "Bài học #$id"
    }

    // Xóa comment rồi reload lại danh sách
    fun deleteComment(comment: Comments) {
        viewModelScope.launch {
            try {
                commentRepo.deleteComment(comment)
                // Reload lại để cập nhật list
                loadComments()
            } catch (e: Exception) {
                _uiState.value = ModerationUiState.Error(e.message ?: "Lỗi xóa bình luận")
            }
        }
    }
}