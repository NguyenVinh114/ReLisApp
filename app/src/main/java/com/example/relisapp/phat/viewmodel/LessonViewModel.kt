package com.example.relisapp.phat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.phat.entity.Lessons
import com.example.relisapp.phat.entity.model.AnswerResult
import com.example.relisapp.phat.entity.model.QuestionWithChoices
import com.example.relisapp.phat.repository.LessonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ViewModel nhận LessonRepository qua constructor
class LessonViewModel(private val repository: LessonRepository) : ViewModel() {

    // --- State cho màn hình Lesson List ---
    private val _lessons = MutableStateFlow<List<Lessons>>(emptyList())
    val lessons: StateFlow<List<Lessons>> = _lessons.asStateFlow()

    // --- State cho màn hình Quiz ---
    private val _questions = MutableStateFlow<List<QuestionWithChoices>>(emptyList())
    val questions: StateFlow<List<QuestionWithChoices>> = _questions.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()


    /**
     * Tải danh sách các bài học cho một category.
     * Vì dùng suspend, dữ liệu sẽ được lấy MỘT LẦN.
     */
    fun loadLessonsForCategory(categoryId: Int) {
        viewModelScope.launch {
            // 1. Gọi hàm suspend và nhận về một List
            val lessonList = repository.getLessonsByCategoryId(categoryId)
            // 2. Cập nhật giá trị cho StateFlow
            _lessons.value = lessonList
        }
    }

    /**
     * Tải danh sách câu hỏi cho màn hình Quiz.
     * Dữ liệu cũng được lấy MỘT LẦN.
     */
    private val _isLoadingQuiz = MutableStateFlow(true) // <-- THÊM STATE MỚI
    val isLoadingQuiz: StateFlow<Boolean> = _isLoadingQuiz.asStateFlow()

    private val _audioPath = MutableStateFlow<String?>(null)
    val audioPath: StateFlow<String?> = _audioPath.asStateFlow()

    fun loadLesson(lessonId: Int) {
        viewModelScope.launch {
            _isLoadingQuiz.value = true
            try {
                val lesson = repository.getLessonById(lessonId)
                _audioPath.value = lesson?.audioPath

                val questionList = repository.getQuestionsWithChoicesForLesson(lessonId)
                _questions.value = questionList
            } finally {
                _isLoadingQuiz.value = false
            }
        }
    }


    /**
     * Nộp bài và tính điểm.
     * Logic này không thay đổi và vẫn đúng.
     * @param selectedAnswers Một Map với key là questionId và value là choiceId người dùng đã chọn.
     */
    private val _quizResults = MutableStateFlow<List<AnswerResult>>(emptyList())
    val quizResults: StateFlow<List<AnswerResult>> = _quizResults.asStateFlow()

    fun submitAnswers(selectedAnswers: Map<Int, String>) {
        var calculatedScore = 0
        val results = mutableListOf<AnswerResult>()

        _questions.value.forEach { questionWithChoices ->
            val question = questionWithChoices.question
            val userAnswer = selectedAnswers[question.questionId]?.trim() // Lấy và cắt khoảng trắng

            // Xác định câu trả lời đúng
            val correctAnswer: String = when (question.questionType) {
                "fill_in_the_blank" -> question.correctAnswer ?: ""
                else -> { // multiple_choice
                    questionWithChoices.choices.find { it.isCorrect == 1 }?.choiceId?.toString() ?: ""
                }
            }

            val isCorrect = userAnswer.equals(correctAnswer, ignoreCase = true)

            if (isCorrect) {
                calculatedScore++
            }

            // Thêm kết quả chi tiết vào danh sách
            results.add(
                AnswerResult(
                    questionId = question.questionId,
                    userAnswer = userAnswer,
                    correctAnswer = correctAnswer,
                    isCorrect = isCorrect
                )
            )
        }

        _score.value = calculatedScore
        _quizResults.value = results // Cập nhật StateFlow kết quả
    }


}
