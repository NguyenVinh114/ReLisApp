// package com.example.relisapp.phat.repository
package com.example.relisapp.phat.repository

import com.example.relisapp.phat.dao.CategoryDao
import com.example.relisapp.phat.entity.Categories

class CategoryRepository(private val categoryDao: CategoryDao) {
    suspend fun getCategoriesForUser(): List<Categories> = categoryDao.getAllForUser()

    suspend fun getCategories(): List<Categories> = categoryDao
        .getAll()
    suspend fun getCategoryById(id: Int): Categories? = categoryDao
        .getById(id)

    suspend fun addCategory(categories: Categories) = categoryDao
        .insert(categories)

    suspend fun updateCategory(categories: Categories) = categoryDao
        .update(categories)

    suspend fun deleteCategory(categories: Categories) = categoryDao
        .delete(categories)

    suspend fun findCategoryByNameAndType(name: String, type: String): Categories? {
        return categoryDao.findByNameAndType(name, type)
    }
}
