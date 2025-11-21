package com.example.relisapp.phat.entity.model

// Data class này lưu trạng thái của một câu trả lời
data class AnswerResult(
    val questionId: Int,
    val userAnswer: String?,
    val correctAnswer: String,
    val isCorrect: Boolean
)

// Enum để thể hiện trạng thái của một lựa chọn sau khi nộp bài
enum class ChoiceState {
    // Trạng thái bình thường, chưa chọn gì
    NEUTRAL,
    // Người dùng chọn đúng
    CORRECT,
    // Người dùng chọn sai
    INCORRECT,
    // Đáp án đúng (nhưng người dùng không chọn)
    SHOW_CORRECT_ANSWER
}
