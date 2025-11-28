package com.example.relisapp.nam.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.relisapp.nam.data.local.SessionManager
import com.example.relisapp.nam.data.repository.UserRepository
import com.example.relisapp.nam.database.AppDatabase
import com.example.relisapp.nam.logic.StreakManager
import com.example.relisapp.nam.viewmodel.StreakViewModel

class StreakViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StreakViewModel::class.java)) {
            // Lấy Application Context để tránh leak
            val appContext = context.applicationContext
            val db = AppDatabase.getDatabase(appContext)

            // 1. Khởi tạo các dependencies cần thiết
            val sessionManager = SessionManager(appContext)
            val userRepository = UserRepository(db.userDao())

            // 2. Khởi tạo StreakManager (Đã cập nhật: cần thêm userRepository)
            val streakManager = StreakManager(
                studySessionDao = db.studySessionDao(),
                userRepository = userRepository
            )

            // 3. Khởi tạo StreakViewModel (Đã cập nhật: cần sessionManager)
            @Suppress("UNCHECKED_CAST")
            return StreakViewModel(streakManager, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}