package com.example.relisapp.data.repository

import com.example.relisapp.data.local.dao.CategoryDao
import com.example.relisapp.data.local.entity.Categories

class CategoryRepository(private val categoryDao: CategoryDao) {
    suspend fun getCategories(): List<Categories> = categoryDao.getAll()
    suspend fun addCategory(categories: Categories) = categoryDao.insert(categories)
}