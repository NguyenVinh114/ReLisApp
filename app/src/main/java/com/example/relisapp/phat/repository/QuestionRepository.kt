package com.example.relisapp.phat.repository

// Đảm bảo bạn đã import đúng QuestionDao và các entity/model cần thiết
import com.example.relisapp.phat.dao.QuestionDao
import com.example.relisapp.phat.entity.Choices
import com.example.relisapp.phat.entity.Questions
import com.example.relisapp.phat.entity.model.QuestionWithChoices
import kotlinx.coroutines.flow.Flow

class QuestionRepository(private val questionDao: QuestionDao) {

    // --- CÁC HÀM CŨ CỦA BẠN (GIỮ NGUYÊN) ---
   /* suspend fun getQuestions(): List<Questions> = questionDao.getAll()
    suspend fun addQuestion(questions: Questions) = questionDao.insert(questions)*/


    // --- [THÊM MỚI] BỔ SUNG HÀM CÒN THIẾU ---
    /**
     * Lấy danh sách các câu hỏi, mỗi câu hỏi đi kèm với các lựa chọn của nó,
     * cho một bài học (lesson) cụ thể.
     * Dữ liệu trả về dưới dạng Flow để tự động cập nhật UI khi có thay đổi.
     */
    fun getQuestionsWithChoicesForLesson(lessonId: Int): Flow<List<QuestionWithChoices>> {
        return questionDao.getQuestionsWithChoicesForLesson(lessonId)
    }

    // --- [THÊM MỚI 1] CÁC HÀM CHO CHỨC NĂNG SỬA ---
    /**
     * Lấy thông tin chi tiết của một câu hỏi duy nhất bằng ID của nó.
     * Dùng cho màn hình AddEditQuestionScreen khi ở chế độ sửa.
     */
    fun getQuestionDetails(questionId: Int): Flow<QuestionWithChoices?> {
        return questionDao.getQuestionWithChoicesById(questionId)
    }

    /**
     * Gọi DAO để cập nhật một câu hỏi và các lựa chọn của nó.
     */
    suspend fun updateQuestion(question: Questions, choices: List<Choices>) {
        questionDao.updateQuestionWithChoices(question, choices)
    }


    // [THÊM MỚI] Hàm để lưu câu hỏi và lựa chọn
    suspend fun saveQuestion(question: Questions, choices: List<Choices>) {
        questionDao.insertQuestionWithChoices(question, choices)
    }
    // Bạn có thể thêm các hàm khác ở đây nếu cần, ví dụ:
    suspend fun deleteQuestion(question: Questions) = questionDao.deleteQuestion(question)
}
