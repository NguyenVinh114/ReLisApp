package com.example.relisapp.nam.ui.screens // (Hoặc package com.example.learn.viewmodel tuỳ cấu trúc bạn)

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.relisapp.nam.data.repository.LessonRepository
import com.example.relisapp.nam.data.repository.LikeRepository
import com.example.relisapp.nam.data.repository.UserRepository
import com.example.relisapp.nam.database.entity.Likes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Data class để hiển thị UI
data class LikeWithDetails(
    val like: Likes,
    val userName: String,
    val lessonTitle: String
)

// State
sealed class LikeModerationUiState {
    object Loading : LikeModerationUiState()
    data class Success(val likes: List<LikeWithDetails>) : LikeModerationUiState()
    data class Error(val message: String) : LikeModerationUiState()
}

class LikeModerationViewModel(
    private val likeRepo: LikeRepository,
    private val userRepo: UserRepository,
    private val lessonRepo: LessonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LikeModerationUiState>(LikeModerationUiState.Loading)
    val uiState: StateFlow<LikeModerationUiState> = _uiState

    init {
        loadLikes()
    }

    fun loadLikes() {
        viewModelScope.launch {
            try {
                _uiState.value = LikeModerationUiState.Loading
                val likes = likeRepo.getAllLikes()

                val enhanced = likes.map { item ->
                    // 1. Lấy thông tin User
                    val user = userRepo.getUserById(item.userId)

                    // 2. Logic hiển thị tên thông minh hơn:
                    // FullName -> Username -> User #ID
                    val displayName = user?.fullName
                        ?: user?.username
                        ?: "User #${item.userId}"

                    // 3. Lấy thông tin bài học
                    val lesson = lessonRepo.getLessonById(item.lessonId)
                    val lessonName = lesson?.title ?: "Bài #${item.lessonId}"

                    LikeWithDetails(
                        like = item,
                        userName = displayName, // ✅ Đã sửa
                        lessonTitle = lessonName
                    )
                }
                _uiState.value = LikeModerationUiState.Success(enhanced)
            } catch (e: Exception) {
                _uiState.value = LikeModerationUiState.Error(e.message ?: "Lỗi tải Like")
            }
        }
    }
}

// Factory giữ nguyên
class LikeModerationViewModelFactory(
    private val likeRepo: LikeRepository,
    private val userRepo: UserRepository,
    private val lessonRepo: LessonRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LikeModerationViewModel::class.java)) {
            return LikeModerationViewModel(likeRepo, userRepo, lessonRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}