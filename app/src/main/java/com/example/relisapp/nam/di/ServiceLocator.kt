package com.example.relisapp.nam.di

import android.content.Context
import com.example.relisapp.nam.data.local.SessionManager
import com.example.relisapp.nam.data.repository.UserRepository
import com.example.relisapp.nam.database.AppDatabase
import com.example.relisapp.nam.viewmodel.AuthViewModelFactory

object ServiceLocator {

    @Volatile
    private var database: AppDatabase? = null

    @Volatile
    private var userRepository: UserRepository? = null

    @Volatile
    private var sessionManager: SessionManager? = null


    fun provideDatabase(context: Context): AppDatabase {
        return database ?: synchronized(this) {
            val instance = AppDatabase.getDatabase(context)
            database = instance
            instance
        }
    }

    fun provideUserRepository(context: Context): UserRepository {
        return userRepository ?: synchronized(this) {
            val db = provideDatabase(context)
            val repo = UserRepository(db.userDao())
            userRepository = repo
            repo
        }
    }

    fun provideSessionManager(context: Context): SessionManager {
        return sessionManager ?: synchronized(this) {
            val instance = SessionManager(context.applicationContext)
            sessionManager = instance
            instance
        }
    }

    fun provideAuthViewModelFactory(context: Context): AuthViewModelFactory {
        val repo = provideUserRepository(context)
        val session = provideSessionManager(context)
        return AuthViewModelFactory(repo, session)
    }
}
