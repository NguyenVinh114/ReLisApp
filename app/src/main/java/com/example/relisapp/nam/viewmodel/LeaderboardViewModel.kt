package com.example.relisapp.nam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.relisapp.nam.data.repository.UserRepository
import com.example.relisapp.nam.database.entity.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LeaderboardViewModel(private val repository: UserRepository) : ViewModel() {

    private val _topUsers = MutableStateFlow<List<User>>(emptyList())
    val topUsers: StateFlow<List<User>> = _topUsers

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadLeaderboard()
    }

    fun loadLeaderboard() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Lấy top 13 user từ DB
                _topUsers.value = repository.getLeaderboardUsers()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class LeaderboardViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LeaderboardViewModel(repository) as T
    }
}