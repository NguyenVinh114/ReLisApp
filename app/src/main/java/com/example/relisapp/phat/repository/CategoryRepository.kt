// package com.example.relisapp.phat.repository
package com.example.relisapp.phat.repository

import com.example.relisapp.phat.dao.CategoryDao
import com.example.relisapp.phat.entity.Categories

class CategoryRepository(private val categoryDao: CategoryDao) {
    suspend fun getCategories(): List<Categories> = categoryDao.getAll()

    // Thêm hàm lấy một category theo ID
    suspend fun getCategoryById(id: Int): Categories? = categoryDao.getById(id)

    suspend fun addCategory(categories: Categories) = categoryDao.insert(categories)

    // Thêm hàm cập nhật
    suspend fun updateCategory(categories: Categories) = categoryDao.update(categories)

    // Thêm hàm xóa
    suspend fun deleteCategory(categories: Categories) = categoryDao.delete(categories)
}
