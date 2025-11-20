package com.example.relisapp.phat.repository

import com.example.relisapp.phat.dao.ProgressDao
import com.example.relisapp.phat.entity.Progress

class ProgressRepository(private val progressDao: ProgressDao) {
    suspend fun getProgressList(): List<Progress> = progressDao.getAll()
    suspend fun addProgress(progress: Progress) = progressDao.insert(progress)
}
