package com.example.relisapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.data.local.entity.Choices
import com.example.relisapp.data.repository.ChoiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChoiceViewModel(private val repo: ChoiceRepository) : ViewModel() {

    private val _choices = MutableStateFlow<List<Choices>>(emptyList())
    val choices: StateFlow<List<Choices>> = _choices

    fun loadChoices() {
        viewModelScope.launch {
            _choices.value = repo.getChoices()
        }
    }

    fun addChoice(choices: Choices) {
        viewModelScope.launch {
            repo.addChoice(choices)
            loadChoices()
        }
    }
}
