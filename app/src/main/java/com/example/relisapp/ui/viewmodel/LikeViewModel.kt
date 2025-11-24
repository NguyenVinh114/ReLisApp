package com.example.relisapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.data.local.entity.Likes
import com.example.relisapp.data.repository.LikeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LikeViewModel(private val repo: LikeRepository) : ViewModel() {

    private val _likes = MutableStateFlow<List<Likes>>(emptyList())
    val likes: StateFlow<List<Likes>> = _likes

    fun loadLikes() {
        viewModelScope.launch {
            _likes.value = repo.getLikes()
        }
    }

    fun addLike(likes: Likes) {
        viewModelScope.launch {
            repo.addLike(likes)
            loadLikes()
        }
    }
}
