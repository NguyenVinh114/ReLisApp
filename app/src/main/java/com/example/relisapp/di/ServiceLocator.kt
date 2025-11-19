package com.example.relisapp.di

import android.content.Context
import com.example.relisapp.data.repository.UserRepository
import com.example.relisapp.data.local.AppDatabase

object ServiceLocator {

    @Volatile
    private var database: AppDatabase? = null

    fun provideDatabase(context: Context): AppDatabase {
        return database ?: synchronized(this) {
            val instance = AppDatabase.getDatabase(context)
            database = instance
            instance
        }
    }

    fun provideUserRepository(context: Context): UserRepository {
        val db = provideDatabase(context)
        return UserRepository(db.userDao())
    }
}
