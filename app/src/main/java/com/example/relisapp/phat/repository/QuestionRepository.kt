package com.example.relisapp.phat.repository

import com.example.relisapp.phat.dao.QuestionDao
import com.example.relisapp.phat.entity.Questions

class QuestionRepository(private val questionDao: QuestionDao) {
    suspend fun getQuestions(): List<Questions> = questionDao.getAll()
    suspend fun addQuestion(questions: Questions) = questionDao.insert(questions)
}
