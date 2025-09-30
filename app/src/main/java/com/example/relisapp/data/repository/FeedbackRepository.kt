package com.example.relisapp.data.repository

import com.example.relisapp.data.local.FeedbackDao
import com.example.relisapp.model.Feedback

class FeedbackRepository(private val feedbackDao: FeedbackDao) {

    suspend fun insertFeedback(feedback: Feedback) = feedbackDao.insertFeedback(feedback)

    suspend fun deleteFeedback(feedback: Feedback) = feedbackDao.deleteFeedback(feedback)

    suspend fun getFeedbacksByUser(userId: Int) = feedbackDao.getFeedbacksByUser(userId)

    suspend fun getAllFeedbacks() = feedbackDao.getAllFeedbacks()
}
