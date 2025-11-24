package com.example.relisapp.nam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.relisapp.nam.data.repository.CommentRepository
import com.example.relisapp.nam.data.repository.LessonRepository
import com.example.relisapp.nam.data.repository.UserRepository
import com.example.relisapp.nam.data.repository.LikeRepository
import com.example.relisapp.nam.viewmodel.CommentViewModel
import com.example.relisapp.nam.viewmodel.LessonViewModel

class ViewModelFactory(
    private val commentRepository: CommentRepository? = null,
    private val lessonRepository: LessonRepository? = null,
    private val userRepository: UserRepository? = null,
    private val likeRepository: LikeRepository? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CommentModerationViewModel::class.java) -> {
                if (commentRepository == null || userRepository == null || lessonRepository == null) {
                    throw IllegalArgumentException("CommentModerationViewModel requires commentRepository, userRepository, and lessonRepository")
                }
                CommentModerationViewModel(commentRepository, userRepository, lessonRepository) as T
            }
            modelClass.isAssignableFrom(CommentViewModel::class.java) -> {
                if (commentRepository == null) {
                    throw IllegalArgumentException("CommentViewModel requires commentRepository")
                }
                CommentViewModel(commentRepository) as T
            }
            modelClass.isAssignableFrom(LessonViewModel::class.java) -> {
                if (lessonRepository == null) {
                    throw IllegalArgumentException("LessonViewModel requires lessonRepository")
                }
                LessonViewModel(lessonRepository) as T
            }
            modelClass.isAssignableFrom(LikeViewModel::class.java) -> {
                if (likeRepository == null) {
                    throw IllegalArgumentException("LikeViewModel requires likeRepository")
                }
                LikeViewModel(likeRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}