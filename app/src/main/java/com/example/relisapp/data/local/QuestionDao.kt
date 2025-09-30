package com.example.relisapp.data.local

import androidx.room.*
import com.example.relisapp.model.Question

@Dao
interface QuestionDao {
    @Insert
    suspend fun insertQuestion(question: Question)

    @Update
    suspend fun updateQuestion(question: Question)

    @Delete
    suspend fun deleteQuestion(question: Question)

    @Query("SELECT * FROM questions WHERE id = :id LIMIT 1")
    suspend fun getQuestionById(id: Int): Question?

    @Query("SELECT * FROM questions WHERE lessonId = :lessonId")
    suspend fun getQuestionsByLessonId(lessonId: Int): List<Question>
}
