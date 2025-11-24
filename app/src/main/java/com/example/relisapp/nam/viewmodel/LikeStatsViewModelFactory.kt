package com.example.relisapp.nam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.relisapp.nam.data.repository.LessonRepository
import com.example.relisapp.nam.data.repository.LikeRepository
import com.example.relisapp.nam.data.repository.UserRepository

class LikeStatsViewModelFactory(
    private val likeRepo: LikeRepository,
    private val userRepo: UserRepository,
    private val lessonRepo: LessonRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LikeStatsViewModel::class.java)) {
            return LikeStatsViewModel(likeRepo, userRepo, lessonRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}