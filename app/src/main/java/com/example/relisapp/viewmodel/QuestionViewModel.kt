package com.example.relisapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.data.local.AppDatabase
import com.example.relisapp.data.repository.QuestionRepository
import com.example.relisapp.model.Question
import kotlinx.coroutines.launch

class QuestionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QuestionRepository

    init {
        val questionDao = AppDatabase.getDatabase(application).questionDao()
        repository = QuestionRepository(questionDao)
    }

    fun insertQuestion(question: Question) = viewModelScope.launch {
        repository.insertQuestion(question)
    }

    fun updateQuestion(question: Question) = viewModelScope.launch {
        repository.updateQuestion(question)
    }

    fun deleteQuestion(question: Question) = viewModelScope.launch {
        repository.deleteQuestion(question)
    }

    suspend fun getQuestionById(id: Int) = repository.getQuestionById(id)

    suspend fun getQuestionsByLessonId(lessonId: Int) = repository.getQuestionsByLessonId(lessonId)
}
