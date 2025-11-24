package com.example.relisapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.data.local.entity.Results
import com.example.relisapp.data.repository.ResultRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ResultViewModel(private val repo: ResultRepository) : ViewModel() {

    private val _results = MutableStateFlow<List<Results>>(emptyList())
    val results: StateFlow<List<Results>> = _results

    fun loadResults() {
        viewModelScope.launch {
            _results.value = repo.getResults()
        }
    }

    fun addResult(results: Results) {
        viewModelScope.launch {
            repo.addResult(results)
            loadResults()
        }
    }
}
