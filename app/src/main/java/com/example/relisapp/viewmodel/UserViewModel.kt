package com.example.relisapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.data.local.AppDatabase
import com.example.relisapp.data.repository.UserRepository
import com.example.relisapp.model.User
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: UserRepository

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
    }

    fun insertUser(user: User) = viewModelScope.launch {
        repository.insertUser(user)
    }

    suspend fun getUserByUsername(username: String) = repository.getUserByUsername(username)

    suspend fun getUserByEmail(email: String) = repository.getUserByEmail(email)

    suspend fun getAllUsers() = repository.getAllUsers()
}
