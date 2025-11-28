// File: com/example/relisapp/phat/viewmodel/QuizViewModel.kt

package com.example.relisapp.phat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.relisapp.nam.data.local.SessionManager
import com.example.relisapp.nam.logic.StreakManager
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
    private val questionRepo: QuestionRepository,
    // ⭐ [MỚI] Thêm StreakManager và SessionManager
    private val streakManager: StreakManager,
    private val sessionManager: SessionManager
) : ViewModel() {

    // --- STATE CHO MÀN HÌNH QUIZ ---
    private val _questions = MutableStateFlow<List<QuestionWithChoices>>(emptyList())
    val questions: StateFlow<List<QuestionWithChoices>> = _questions.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    private val _isLoadingQuiz = MutableStateFlow(true)
    val isLoadingQuiz: StateFlow<Boolean> = _isLoadingQuiz.asStateFlow()

    private val _audioPath = MutableStateFlow<String?>(null)
    val audioPath: StateFlow<String?> = _audioPath.asStateFlow()

    private val _lessonContent = MutableStateFlow<String?>(null)
    val lessonContent: StateFlow<String?> = _lessonContent.asStateFlow()

    private val _quizResults = MutableStateFlow<List<AnswerResult>>(emptyList())
    val quizResults: StateFlow<List<AnswerResult>> = _quizResults.asStateFlow()

    /**
     * Tải dữ liệu bài học và câu hỏi
     */
    fun loadQuizData(lessonId: Int) {
        if (lessonId == -1) {
            _isLoadingQuiz.value = false
            return
        }

        viewModelScope.launch {
            _isLoadingQuiz.value = true

            // Reset dữ liệu cũ
            _questions.value = emptyList()
            _audioPath.value = null
            _lessonContent.value = null
            _quizResults.value = emptyList()
            _score.value = 0

            try {
                // Lấy thông tin bài học
                val lesson = lessonRepo.getLessonById(lessonId).first()
                _audioPath.value = lesson?.audioPath
                _lessonContent.value = lesson?.content

                // Lấy danh sách câu hỏi
                val questionsList = questionRepo.getQuestionsWithChoicesForLesson(lessonId).first()
                _questions.value = questionsList

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoadingQuiz.value = false
            }
        }
    }

    /**
     * Xử lý nộp bài: Chấm điểm + CẬP NHẬT STREAK
     */
    fun submitAnswers(selectedAnswers: Map<Int, String>) {
        var calculatedScore = 0
        val results = mutableListOf<AnswerResult>()
        val totalQuestions = _questions.value.size

        // 1. Chấm điểm
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

        // Cập nhật State Score
        _score.value = calculatedScore
        _quizResults.value = results

        // 2. ⭐ TÍNH STREAK (Logic Mới)
        // Chỉ cần user làm bài (có điểm > 0 hoặc hoàn thành) là tính streak
        val finalScorePercent = if (totalQuestions > 0) (calculatedScore * 100f / totalQuestions) else 0f

        // Gọi update streak
        updateUserStreak(finalScorePercent)
    }

    private fun updateUserStreak(score: Float) {
        viewModelScope.launch {
            val userId = sessionManager.getUserId()
            if (userId != -1) {
                try {
                    streakManager.recordStudySession(
                        userId = userId,
                        lessonsCompleted = 1,
                        isListening = true, // Hoặc logic tùy loại bài
                        timeMinutes = 5,    // Giả định
                        score = score
                    )
                    // DB đã được update, Streak Activity sẽ tự hiển thị đúng
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}

// ⭐ CẬP NHẬT FACTORY Ở CUỐI FILE ĐỂ NHẬN THÊM DEPENDENCIES
class QuizViewModelFactory(
    private val lessonRepo: LessonRepository,
    private val questionRepo: QuestionRepository,
    private val streakManager: StreakManager,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuizViewModel(
                lessonRepo,
                questionRepo,
                streakManager,
                sessionManager
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}