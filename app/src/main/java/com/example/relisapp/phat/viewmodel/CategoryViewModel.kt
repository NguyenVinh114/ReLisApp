// package com.example.relisapp.phat.viewmodel
package com.example.relisapp.phat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.phat.entity.Categories
import com.example.relisapp.phat.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed class SaveResult {
    object Success : SaveResult()
    data class Existed(val message: String) : SaveResult()
    data class Failure(val error: Throwable) : SaveResult()
}

class CategoryViewModel(private val repo: CategoryRepository) : ViewModel() {

    private val _categories = MutableStateFlow<List<Categories>>(emptyList())
    val categories: StateFlow<List<Categories>> = _categories

    private val _categoriesForUser = MutableStateFlow<List<Categories>>(emptyList())
    val categoriesForUser: StateFlow<List<Categories>> = _categoriesForUser

    private val _currentCategory = MutableStateFlow<Categories?>(null)
    val currentCategory: StateFlow<Categories?> = _currentCategory

    init {
        loadCategories()
        loadCategoriesForUser()
    }

    fun loadCategoriesForUser() {
        viewModelScope.launch {
            _categoriesForUser.value = repo.getCategoriesForUser()
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            _categories.value = repo.getCategories()
        }
    }
    fun loadCategoryById(id: Int) {
        viewModelScope.launch {
            _currentCategory.value = repo.getCategoryById(id)
        }
    }

    fun clearCurrentCategory() {
        _currentCategory.value = null
    }

    private val _saveResult = MutableSharedFlow<SaveResult>()
    val saveResult = _saveResult.asSharedFlow()

    fun addCategory(categoryToAdd: Categories) {
        viewModelScope.launch {
            val existing = repo.findCategoryByNameAndType(categoryToAdd.categoryName, categoryToAdd.type)
            if (existing == null) {
                repo.addCategory(categoryToAdd)
                loadCategories()
                _saveResult.emit(SaveResult.Success)
            } else {
                _saveResult.emit(SaveResult.Existed("Category name already exists for this type."))
            }
        }
    }

    fun updateCategory(categoryToUpdate: Categories) {
        viewModelScope.launch {
            val existing = repo.findCategoryByNameAndType(categoryToUpdate.categoryName, categoryToUpdate.type)
            if (existing == null || existing.categoryId == categoryToUpdate.categoryId) {
                repo.updateCategory(categoryToUpdate)
                loadCategories()
                _saveResult.emit(SaveResult.Success)
            } else {
                _saveResult.emit(SaveResult.Existed("Category name already exists for this type."))
            }
        }
    }


    fun lockCategory(categoryToLock: Categories) {
        viewModelScope.launch {

            val lockedCategory = categoryToLock.copy(isLocked = 1)
            repo.updateCategory(lockedCategory)
            loadCategories()
        }
    }

    fun unLockCategory(categoryToLock: Categories) {
        viewModelScope.launch {
            val unLockedCategory = categoryToLock.copy(isLocked = 0)
            repo.updateCategory(unLockedCategory)
            loadCategories()
        }
    }


}
