package com.example.relisapp.nam.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.relisapp.nam.data.local.SessionManager
import com.example.relisapp.nam.data.repository.UserRepository
import com.example.relisapp.nam.database.AppDatabase
import com.example.relisapp.nam.logic.StreakManager
import com.example.relisapp.nam.viewmodel.AuthViewModelFactory
import com.example.relisapp.nam.viewmodel.StreakViewModel

object ViewModelProviderFactory {

    // ========================================================================
    // 1. Cho Auth (Login, Register, AdminDashboard, Profile...)
    // ========================================================================
    fun provideAuthViewModelFactory(context: Context): AuthViewModelFactory {
        val repo = ServiceLocator.provideUserRepository(context)
        val session = ServiceLocator.provideSessionManager(context)
        return AuthViewModelFactory(repo, session)
    }

    // ========================================================================
    // 2. Cho Streak (Màn hình thành tích)
    // ========================================================================
    class StreakViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StreakViewModel::class.java)) {
                // Lấy Application Context để tránh leak
                val appContext = context.applicationContext
                val db = AppDatabase.getDatabase(appContext)

                // Khởi tạo dependencies
                val sessionManager = SessionManager(appContext)
                val userRepository = UserRepository(db.userDao())

                // Khởi tạo StreakManager với đầy đủ tham số
                val streakManager = StreakManager(
                    studySessionDao = db.studySessionDao(),
                    userRepository = userRepository
                )

                return StreakViewModel(streakManager, sessionManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}