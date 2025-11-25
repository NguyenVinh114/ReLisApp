package com.example.relisapp.data.repository

import com.example.relisapp.data.local.dao.QuestionDao
import com.example.relisapp.data.local.entity.Questions

class QuestionRepository(private val questionDao: QuestionDao) {
    suspend fun getQuestions(): List<Questions> = questionDao.getAll()
    suspend fun addQuestion(questions: Questions) = questionDao.insert(questions)
}
