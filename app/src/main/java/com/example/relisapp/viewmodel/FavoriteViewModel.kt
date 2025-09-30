package com.example.relisapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.data.local.AppDatabase
import com.example.relisapp.data.repository.FavoriteRepository
import com.example.relisapp.model.Favorite
import kotlinx.coroutines.launch

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FavoriteRepository

    init {
        val favoriteDao = AppDatabase.getDatabase(application).favoriteDao()
        repository = FavoriteRepository(favoriteDao)
    }

    fun insertFavorite(favorite: Favorite) = viewModelScope.launch {
        repository.insertFavorite(favorite)
    }

    fun deleteFavorite(favorite: Favorite) = viewModelScope.launch {
        repository.deleteFavorite(favorite)
    }

    suspend fun getFavoritesByUser(userId: Int) = repository.getFavoritesByUser(userId)

    suspend fun getFavorite(userId: Int, lessonId: Int) = repository.getFavorite(userId, lessonId)
}
