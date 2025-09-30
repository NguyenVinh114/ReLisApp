package com.example.relisapp.data.local

import androidx.room.*
import com.example.relisapp.model.Feedback

@Dao
interface FeedbackDao {
    @Insert
    suspend fun insertFeedback(feedback: Feedback)

    @Delete
    suspend fun deleteFeedback(feedback: Feedback)

    @Query("SELECT * FROM feedbacks WHERE userId = :userId")
    suspend fun getFeedbacksByUser(userId: Int): List<Feedback>

    @Query("SELECT * FROM feedbacks")
    suspend fun getAllFeedbacks(): List<Feedback>
}
