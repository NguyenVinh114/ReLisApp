package com.example.relisapp.nam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.relisapp.nam.data.repository.CommentRepository
import com.example.relisapp.nam.data.repository.LessonRepository
import com.example.relisapp.nam.data.repository.UserRepository

class CommentModerationViewModelFactory(
    private val commentRepo: CommentRepository,
    private val userRepo: UserRepository,
    private val lessonRepo: LessonRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommentModerationViewModel::class.java)) {
            return CommentModerationViewModel(commentRepo, userRepo, lessonRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}