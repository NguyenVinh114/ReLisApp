package com.example.relisapp.data.repository

import com.example.relisapp.data.local.dao.ProgressDao
import com.example.relisapp.data.local.entity.Progress

class ProgressRepository(private val progressDao: ProgressDao) {
    suspend fun getProgressList(): List<Progress> = progressDao.getAll()
    suspend fun addProgress(progress: Progress) = progressDao.insert(progress)

}
