// package com.example.relisapp.phat.viewmodel
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

    // StateFlow để giữ category đang được sửa
    private val _currentCategory = MutableStateFlow<Categories?>(null)
    val currentCategory: StateFlow<Categories?> = _currentCategory

    init {
        loadCategories() // Tải danh sách khi ViewModel được tạo
    }

    fun loadCategories() {
        viewModelScope.launch {
            _categories.value = repo.getCategories()
        }
    }

    // Tải một category cụ thể bằng ID
    fun loadCategoryById(id: Int) {
        viewModelScope.launch {
            _currentCategory.value = repo.getCategoryById(id)
        }
    }

    // Reset category hiện tại khi không cần nữa (ví dụ khi rời màn hình edit)
    fun clearCurrentCategory() {
        _currentCategory.value = null
    }

    fun addCategory(categories: Categories) {
        viewModelScope.launch {
            repo.addCategory(categories)
            loadCategories() // Tải lại danh sách sau khi thêm
        }
    }

    // Thêm hàm cập nhật
    fun updateCategory(categories: Categories) {
        viewModelScope.launch {
            repo.updateCategory(categories)
            loadCategories() // Tải lại danh sách sau khi cập nhật
        }
    }

    // Thêm hàm xóa
    fun deleteCategory(categories: Categories) {
        viewModelScope.launch {
            repo.deleteCategory(categories)
            loadCategories() // Tải lại danh sách sau khi xóa
        }
    }
}
