package com.example.relisapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.data.local.entity.FavoriteLessons
import com.example.relisapp.data.repository.FavoriteLessonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoriteLessonViewModel(private val repo: FavoriteLessonRepository) : ViewModel() {

    private val _favorites = MutableStateFlow<List<FavoriteLessons>>(emptyList())
    val favorites: StateFlow<List<FavoriteLessons>> = _favorites

    fun loadFavorites() {
        viewModelScope.launch {
            _favorites.value = repo.getFavorites()
        }
    }

    fun addFavorite(fav: FavoriteLessons) {
        viewModelScope.launch {
            repo.addFavorite(fav)
            loadFavorites()
        }
    }
}
