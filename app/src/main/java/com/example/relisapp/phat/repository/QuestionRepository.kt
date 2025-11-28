package com.example.relisapp.phat.repository

// Đảm bảo bạn đã import đúng QuestionDao và các entity/model cần thiết
import com.example.relisapp.phat.dao.QuestionDao
import com.example.relisapp.phat.entity.Choices
import com.example.relisapp.phat.entity.Questions
import com.example.relisapp.phat.entity.model.QuestionWithChoices
import kotlinx.coroutines.flow.Flow

class QuestionRepository(private val questionDao: QuestionDao) {

    fun getQuestionsWithChoicesForLesson(lessonId: Int): Flow<List<QuestionWithChoices>> {
        return questionDao.getQuestionsWithChoicesForLesson(lessonId)
    }

    fun getQuestionDetails(questionId: Int): Flow<QuestionWithChoices?> {
        return questionDao.getQuestionWithChoicesById(questionId)
    }

    suspend fun deleteQuestion(question: Questions) = questionDao.deleteQuestion(question)

    suspend fun saveQuestion(question: Questions, choices: List<Choices>) {
        questionDao.saveNewQuestion(question, choices)
    }

    suspend fun updateQuestion(question: Questions, choices: List<Choices>) {
        questionDao.updateExistingQuestion(question, choices)
    }

    suspend fun doesQuestionExist(questionText: String, lessonId: Int): Boolean {
        return questionDao.questionExists(questionText, lessonId)
    }

    suspend fun doesQuestionExist(questionText: String, lessonId: Int, excludeQuestionId: Int): Boolean {
        return questionDao.questionExistsExcludingId(questionText, lessonId, excludeQuestionId)
    }

}
