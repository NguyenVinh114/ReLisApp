package com.example.relisapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.data.local.entity.Questions
import com.example.relisapp.data.repository.QuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuestionViewModel(private val repo: QuestionRepository) : ViewModel() {

    private val _questions = MutableStateFlow<List<Questions>>(emptyList())
    val questions: StateFlow<List<Questions>> = _questions

    fun loadQuestions() {
        viewModelScope.launch {
            _questions.value = repo.getQuestions()
        }
    }

    fun addQuestion(questions: Questions) {
        viewModelScope.launch {
            repo.addQuestion(questions)
            loadQuestions()
        }
    }
}
