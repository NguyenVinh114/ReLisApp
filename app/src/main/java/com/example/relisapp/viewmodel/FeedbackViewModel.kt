package com.example.relisapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.data.local.AppDatabase
import com.example.relisapp.data.repository.FeedbackRepository
import com.example.relisapp.model.Feedback
import kotlinx.coroutines.launch

class FeedbackViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FeedbackRepository

    init {
        val feedbackDao = AppDatabase.getDatabase(application).feedbackDao()
        repository = FeedbackRepository(feedbackDao)
    }

    fun insertFeedback(feedback: Feedback) = viewModelScope.launch {
        repository.insertFeedback(feedback)
    }

    fun deleteFeedback(feedback: Feedback) = viewModelScope.launch {
        repository.deleteFeedback(feedback)
    }

    suspend fun getFeedbacksByUser(userId: Int) = repository.getFeedbacksByUser(userId)

    suspend fun getAllFeedbacks() = repository.getAllFeedbacks()
}
