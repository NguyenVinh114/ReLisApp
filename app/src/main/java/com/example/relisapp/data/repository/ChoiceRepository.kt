package com.example.relisapp.data.repository

import com.example.relisapp.data.local.dao.ChoiceDao
import com.example.relisapp.data.local.entity.Choices

class ChoiceRepository(private val choiceDao: ChoiceDao) {
    suspend fun getChoices(): List<Choices> = choiceDao.getAll()
    suspend fun addChoice(choices: Choices) = choiceDao.insert(choices)
}
