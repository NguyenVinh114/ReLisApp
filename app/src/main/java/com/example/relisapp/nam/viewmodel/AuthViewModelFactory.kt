package com.example.relisapp.nam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.relisapp.nam.data.repository.UserRepository
import com.example.relisapp.nam.data.local.SessionManager

@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory(
    private val repository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(repository, sessionManager) as T
    }
}


