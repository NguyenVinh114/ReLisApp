package com.example.relisapp.phat.repository

import com.example.relisapp.phat.dao.ResultDao
import com.example.relisapp.phat.entity.Results

class ResultRepository(private val resultDao: ResultDao) {
    suspend fun getResults(): List<Results> = resultDao.getAll()
    suspend fun addResult(results: Results) = resultDao.insert(results)
}
