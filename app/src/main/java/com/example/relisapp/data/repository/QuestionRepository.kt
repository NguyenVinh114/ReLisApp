package com.example.relisapp.data.repository

import com.example.relisapp.data.local.QuestionDao
import com.example.relisapp.model.Question

class QuestionRepository(private val questionDao: QuestionDao) {

    suspend fun insertQuestion(question: Question) = questionDao.insertQuestion(question)

    suspend fun updateQuestion(question: Question) = questionDao.updateQuestion(question)

    suspend fun deleteQuestion(question: Question) = questionDao.deleteQuestion(question)

    suspend fun getQuestionById(id: Int) = questionDao.getQuestionById(id)

    suspend fun getQuestionsByLessonId(lessonId: Int) = questionDao.getQuestionsByLessonId(lessonId)
}
