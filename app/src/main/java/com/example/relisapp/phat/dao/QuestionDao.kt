// In file: phat/dao/QuestionDao.kt

package com.example.relisapp.phat.dao

import androidx.room.*
import com.example.relisapp.phat.entity.Choices
import com.example.relisapp.phat.entity.Questions
import com.example.relisapp.phat.entity.model.QuestionWithChoices
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: Questions): Long // Trả về ID của câu hỏi mới

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChoices(choices: List<Choices>)

    @Transaction
    suspend fun insertQuestionWithChoices(question: Questions, choices: List<Choices>) {
        val questionIdAsLong = insertQuestion(question)
        val questionIdAsInt = questionIdAsLong.toInt()
        val choicesWithQuestionId = choices.map { it.copy(questionId = questionIdAsInt) }
        if (choicesWithQuestionId.isNotEmpty()) {
            insertChoices(choicesWithQuestionId)
        }
    }


    @Transaction
    @Query("SELECT * FROM Questions WHERE lessonId = :lessonId ORDER BY questionId ASC")
    fun getQuestionsWithChoicesForLesson(lessonId: Int): Flow<List<QuestionWithChoices>>

    // --- [THÊM MỚI 1] ---
    // Lấy chi tiết một câu hỏi duy nhất (Dùng cho AddEditQuestionScreen ở chế độ sửa)
    @Transaction
    @Query("SELECT * FROM Questions WHERE questionId = :questionId")
    fun getQuestionWithChoicesById(questionId: Int): Flow<QuestionWithChoices?>

    @Update
    suspend fun updateQuestion(question: Questions)

    @Query("DELETE FROM Choices WHERE questionId = :questionId")
    suspend fun deleteChoicesForQuestion(questionId: Int)

    @Transaction
    suspend fun updateQuestionWithChoices(question: Questions, choices: List<Choices>) {
        updateQuestion(question)

        deleteChoicesForQuestion(question.questionId)

        val choicesWithQuestionId = choices.map { it.copy(questionId = question.questionId) }
        if (choicesWithQuestionId.isNotEmpty()) {
            insertChoices(choicesWithQuestionId)
        }
    }

    @Delete
    suspend fun deleteQuestion(question: Questions)

    // [THÊM MỚI 1] Kiểm tra xem có câu hỏi nào với `questionText` và `lessonId` đã cho không
    @Query("SELECT EXISTS(SELECT 1 FROM Questions WHERE questionText = :questionText AND lessonId = :lessonId LIMIT 1)")
    suspend fun questionExists(questionText: String, lessonId: Int): Boolean

    // [THÊM MỚI 2] Tương tự như trên, nhưng loại trừ một `questionId` cụ thể (dùng khi SỬA)
    @Query("SELECT EXISTS(SELECT 1 FROM Questions WHERE questionText = :questionText AND lessonId = :lessonId AND questionId != :excludeQuestionId LIMIT 1)")
    suspend fun questionExistsExcludingId(questionText: String, lessonId: Int, excludeQuestionId: Int): Boolean

    @Transaction
    suspend fun saveNewQuestion(question: Questions, choices: List<Choices>) {
        val newQuestionId = insertQuestion(question)
        val choicesWithQuestionId = choices.map { it.copy(questionId = newQuestionId.toInt()) }
        insertChoices(choicesWithQuestionId)
    }

    @Transaction
    suspend fun updateExistingQuestion(question: Questions, choices: List<Choices>) {
        updateQuestion(question)
        deleteChoicesForQuestion(question.questionId)
        val choicesWithQuestionId = choices.map { it.copy(questionId = question.questionId) }
        insertChoices(choicesWithQuestionId)
    }
}
