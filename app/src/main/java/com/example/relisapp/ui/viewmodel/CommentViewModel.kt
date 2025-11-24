package com.example.relisapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.data.local.entity.Comments
import com.example.relisapp.data.local.entity.model.CommentWithUser
import com.example.relisapp.data.repository.CommentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommentViewModel(private val repo: CommentRepository) : ViewModel() {

    // State giờ là List<CommentWithUser> để hiển thị được tên
    private val _comments = MutableStateFlow<List<CommentWithUser>>(emptyList())
    val comments: StateFlow<List<CommentWithUser>> = _comments

    // Hàm load comment theo lessonId
    fun loadComments(lessonId: Int) {
        viewModelScope.launch {
            _comments.value = repo.getCommentsByLesson(lessonId)
        }
    }

    fun addComment(userId: Int, lessonId: Int, content: String) {
        if (content.isBlank()) return

        // Tạo ngày giờ hiện tại
        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

        val newComment = Comments(
            userId = userId,
            lessonId = lessonId,
            content = content,
            createdAt = currentDate
        )

        viewModelScope.launch {
            repo.addComment(newComment)
            // Sau khi thêm xong, gọi load lại ngay lập tức để cập nhật danh sách
            loadComments(lessonId)
        }
    }
}