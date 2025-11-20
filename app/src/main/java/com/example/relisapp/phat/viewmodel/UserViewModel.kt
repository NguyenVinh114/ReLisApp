package com.example.relisapp.phat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.phat.entity.Users
import com.example.relisapp.phat.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val repo: UserRepository) : ViewModel() {

    private val _users = MutableStateFlow<List<Users>>(emptyList())
    val users: StateFlow<List<Users>> = _users

    fun loadUsers() {
        viewModelScope.launch {
            _users.value = repo.getUsers()
        }
    }

    fun addUser(users: Users) {
        viewModelScope.launch {
            repo.addUser(users)
            loadUsers()
        }
    }
}
