package com.example.relisapp.nam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.nam.data.repository.LessonRepository
import com.example.relisapp.nam.data.repository.LikeRepository
import com.example.relisapp.nam.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Data models cho thống kê
data class LessonLikeStats(val lessonId: Int, val lessonTitle: String, val count: Int)
data class UserLikeStats(val userId: Int, val userName: String, val count: Int)
data class RecentLikeItem(val userName: String, val lessonTitle: String, val time: String)

// UiState
sealed class LikeStatsUiState {
    object Loading : LikeStatsUiState()
    data class Success(
        val totalLikes: Int,
        val topLessons: List<LessonLikeStats>,
        val topUsers: List<UserLikeStats>,
        val recentLikes: List<RecentLikeItem>
    ) : LikeStatsUiState()
    data class Error(val message: String) : LikeStatsUiState()
}

class LikeStatsViewModel(
    private val likeRepo: LikeRepository,
    private val userRepo: UserRepository,
    private val lessonRepo: LessonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LikeStatsUiState>(LikeStatsUiState.Loading)
    val uiState: StateFlow<LikeStatsUiState> = _uiState

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            try {
                _uiState.value = LikeStatsUiState.Loading

                // 1. Lấy toàn bộ dữ liệu raw
                val allLikes = likeRepo.getAllLikes()
                val total = allLikes.size

                // 2. Tính Top Bài Học (Group by lessonId)
                val topLessons = allLikes.groupBy { it.lessonId }
                    .map { (id, list) ->
                        val title = lessonRepo.getLessonById(id)?.title ?: "Bài #$id"
                        LessonLikeStats(id, title, list.size)
                    }
                    .sortedByDescending { it.count }
                    .take(5) // Lấy top 5

                // 3. Tính Top User (Group by userId)
                val topUsers = allLikes.groupBy { it.userId }
                    .map { (id, list) ->
                        val user = userRepo.getUserById(id)
                        // ✅ Logic hiển thị tên chuẩn: FullName -> Username -> ID
                        val name = user?.fullName ?: user?.username ?: "User #$id"
                        UserLikeStats(id, name, list.size)
                    }
                    .sortedByDescending { it.count }
                    .take(5) // Lấy top 5

                // 4. Lấy Like gần đây nhất (Recent)
                val recent = allLikes.take(10).map { like ->
                    val user = userRepo.getUserById(like.userId)
                    val lesson = lessonRepo.getLessonById(like.lessonId)

                    // ✅ ĐÃ SỬA LỖI Ở ĐÂY: Thêm fallback vào username
                    val displayName = user?.fullName
                        ?: user?.username
                        ?: "User #${like.userId}"

                    RecentLikeItem(
                        userName = displayName,
                        lessonTitle = lesson?.title ?: "Bài #${like.lessonId}",
                        time = like.likedAt ?: "-"
                    )
                }

                _uiState.value = LikeStatsUiState.Success(total, topLessons, topUsers, recent)

            } catch (e: Exception) {
                _uiState.value = LikeStatsUiState.Error(e.message ?: "Lỗi tính toán thống kê")
            }
        }
    }
}