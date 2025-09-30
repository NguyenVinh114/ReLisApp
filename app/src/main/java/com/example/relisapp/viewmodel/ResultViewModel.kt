package com.example.relisapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.data.local.AppDatabase
import com.example.relisapp.data.repository.ResultRepository
import com.example.relisapp.model.Result
import kotlinx.coroutines.launch

class ResultViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ResultRepository

    init {
        val resultDao = AppDatabase.getDatabase(application).resultDao()
        repository = ResultRepository(resultDao)
    }

    fun insertResult(result: Result) = viewModelScope.launch {
        repository.insertResult(result)
    }

    fun updateResult(result: Result) = viewModelScope.launch {
        repository.updateResult(result)
    }

    fun deleteResult(result: Result) = viewModelScope.launch {
        repository.deleteResult(result)
    }

    suspend fun getResultsByUser(userId: Int) = repository.getResultsByUser(userId)

    suspend fun getResultsByLesson(lessonId: Int) = repository.getResultsByLesson(lessonId)
}
