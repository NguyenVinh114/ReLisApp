package com.example.relisapp.phat.repository

import com.example.relisapp.phat.dao.ChoiceDao
import com.example.relisapp.phat.entity.Choices

class ChoiceRepository(private val choiceDao: ChoiceDao) {
    suspend fun getChoices(): List<Choices> = choiceDao.getAll()
    suspend fun addChoice(choices: Choices) = choiceDao.insert(choices)
}
