package com.example.relisapp.phat.repository

import com.example.relisapp.phat.dao.CategoryDao
import com.example.relisapp.phat.entity.Categories

class CategoryRepository(private val categoryDao: CategoryDao) {
    suspend fun getCategories(): List<Categories> = categoryDao.getAll()
    suspend fun addCategory(categories: Categories) = categoryDao.insert(categories)
}