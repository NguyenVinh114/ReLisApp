package com.example.relisapp.data.repository

import com.example.relisapp.data.local.ResultDao
import com.example.relisapp.model.Result

class ResultRepository(private val resultDao: ResultDao) {

    suspend fun insertResult(result: Result) = resultDao.insertResult(result)

    suspend fun updateResult(result: Result) = resultDao.updateResult(result)

    suspend fun deleteResult(result: Result) = resultDao.deleteResult(result)

    suspend fun getResultsByUser(userId: Int) = resultDao.getResultsByUser(userId)

    suspend fun getResultsByLesson(lessonId: Int) = resultDao.getResultsByLesson(lessonId)
}
