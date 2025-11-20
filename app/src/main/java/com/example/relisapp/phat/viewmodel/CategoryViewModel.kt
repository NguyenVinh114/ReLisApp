package com.example.relisapp.phat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.phat.entity.Categories
import com.example.relisapp.phat.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(private val repo: CategoryRepository) : ViewModel() {

    private val _categories = MutableStateFlow<List<Categories>>(emptyList())
    val categories: StateFlow<List<Categories>> = _categories

    fun loadCategories() {
        viewModelScope.launch {
            _categories.value = repo.getCategories()
        }
    }

    fun addCategory(categories: Categories) {
        viewModelScope.launch {
            repo.addCategory(categories)
            loadCategories()
        }
    }
}
