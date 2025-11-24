// In file: phat/viewmodel/QuizViewModel.kt

package com.example.relisapp.phat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.relisapp.phat.entity.model.AnswerResult
import com.example.relisapp.phat.entity.model.QuestionWithChoices
import com.example.relisapp.phat.repository.LessonRepository
import com.example.relisapp.phat.repository.QuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class QuizViewModel(
    private val lessonRepo: LessonRepository,
    private val questionRepo: QuestionRepository
) : ViewModel() {

    // --- STATE CHO MÀN HÌNH QUIZ ---
    private val _questions = MutableStateFlow<List<QuestionWithChoices>>(emptyList())
    val questions: StateFlow<List<QuestionWithChoices>> = _questions.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    // Khởi tạo trạng thái loading là true
    private val _isLoadingQuiz = MutableStateFlow(true)
    val isLoadingQuiz: StateFlow<Boolean> = _isLoadingQuiz.asStateFlow()

    private val _audioPath = MutableStateFlow<String?>(null)
    val audioPath: StateFlow<String?> = _audioPath.asStateFlow()

    private val _lessonContent = MutableStateFlow<String?>(null)
    val lessonContent: StateFlow<String?> = _lessonContent.asStateFlow()

    private val _quizResults = MutableStateFlow<List<AnswerResult>>(emptyList())
    val quizResults: StateFlow<List<AnswerResult>> = _quizResults.asStateFlow()

    /**
     * Tải tất cả dữ liệu cần thiết cho một bài quiz.
     * Đây là hàm quan trọng nhất để sửa lỗi "quay hoài".
     */
    fun loadQuizData(lessonId: Int) {
        if (lessonId == -1) {
            _isLoadingQuiz.value = false // Dừng loading nếu ID không hợp lệ
            return
        }

        viewModelScope.launch {
            _isLoadingQuiz.value = true // Bật loading khi bắt đầu

            // Reset dữ liệu cũ để không hiển thị dư liệu của quiz trước đó
            _questions.value = emptyList()
            _audioPath.value = null
            _lessonContent.value = null
            _quizResults.value = emptyList()
            _score.value = 0

            try {
                // Lấy thông tin bài học (audio, content)
                val lesson = lessonRepo.getLessonById(lessonId).first()
                _audioPath.value = lesson?.audioPath
                _lessonContent.value = lesson?.content

                // Lấy danh sách câu hỏi
                val questionsList = questionRepo.getQuestionsWithChoicesForLesson(lessonId).first()
                _questions.value = questionsList

            } catch (e: Exception) {
                // In lỗi ra logcat để dễ dàng debug
                e.printStackTrace()
                // Bạn có thể set một State lỗi ở đây để hiển thị thông báo trên UI
            } finally {
                // Quan trọng: Luôn tắt loading sau khi hoàn tất, dù thành công hay thất bại
                _isLoadingQuiz.value = false
            }
        }
    }

    /**
     * Xử lý việc nộp bài và chấm điểm.
     */
    fun submitAnswers(selectedAnswers: Map<Int, String>) {
        var calculatedScore = 0
        val results = mutableListOf<AnswerResult>()

        _questions.value.forEach { questionWithChoices ->
            val question = questionWithChoices.question
            val userAnswer = selectedAnswers[question.questionId]?.trim()

            val correctAnswerText: String = when (question.questionType) {
                "fill_in_the_blank" -> question.correctAnswer ?: ""
                else -> { // multiple_choice
                    questionWithChoices.choices.find { it.isCorrect == 1 }?.choiceText ?: ""
                }
            }

            val isCorrect = userAnswer.equals(correctAnswerText, ignoreCase = true)

            if (isCorrect) {
                calculatedScore++
            }

            results.add(
                AnswerResult(
                    questionId = question.questionId,
                    userAnswer = userAnswer,
                    correctAnswer = correctAnswerText,
                    isCorrect = isCorrect
                )
            )
        }

        _score.value = calculatedScore
        _quizResults.value = results
    }
}

// Factory không cần thay đổi
class QuizViewModelFactory(
    private val lessonRepository: LessonRepository,
    private val questionRepository: QuestionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuizViewModel(lessonRepository, questionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class for Quiz")
    }
}
