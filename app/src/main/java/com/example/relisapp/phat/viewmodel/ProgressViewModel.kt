package com.example.relisapp.phat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.phat.entity.Progress
import com.example.relisapp.phat.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProgressViewModel(private val repo: ProgressRepository) : ViewModel() {

    private val _progressList = MutableStateFlow<List<Progress>>(emptyList())
    val progressList: StateFlow<List<Progress>> = _progressList

    fun loadProgress() {
        viewModelScope.launch {
            _progressList.value = repo.getProgressList()
        }
    }

    fun addProgress(progress: Progress) {
        viewModelScope.launch {
            repo.addProgress(progress)
            loadProgress()
        }
    }
}
