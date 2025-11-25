package com.example.relisapp.data.repository

import com.example.relisapp.data.local.dao.ResultDao
import com.example.relisapp.data.local.entity.Results

class ResultRepository(private val resultDao: ResultDao) {
    suspend fun getResults(): List<Results> = resultDao.getAll()
    suspend fun addResult(results: Results) = resultDao.insertResult(results)
}
