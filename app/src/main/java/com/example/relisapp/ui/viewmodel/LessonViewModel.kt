package com.example.relisapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.data.local.entity.Comments
import com.example.relisapp.data.local.entity.Lessons
import com.example.relisapp.data.local.entity.Results
import com.example.relisapp.data.local.entity.model.AnswerResult
import com.example.relisapp.data.local.entity.model.QuestionWithChoices
import com.example.relisapp.data.repository.LessonRepository
import com.example.relisapp.ui.user.screen.UserComment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LessonViewModel(private val repository: LessonRepository) : ViewModel() {

    // --- State cho màn hình Lesson List ---
    private val _lessons = MutableStateFlow<List<Lessons>>(emptyList())
    val lessons: StateFlow<List<Lessons>> = _lessons.asStateFlow()

    // --- State cho màn hình Quiz ---
    private val _questions = MutableStateFlow<List<QuestionWithChoices>>(emptyList())
    val questions: StateFlow<List<QuestionWithChoices>> = _questions.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    private val _isLoadingQuiz = MutableStateFlow(true)
    val isLoadingQuiz: StateFlow<Boolean> = _isLoadingQuiz.asStateFlow()

    private val _audioPath = MutableStateFlow<String?>(null)
    val audioPath: StateFlow<String?> = _audioPath.asStateFlow()

    private val _quizResults = MutableStateFlow<List<AnswerResult>>(emptyList())
    val quizResults: StateFlow<List<AnswerResult>> = _quizResults.asStateFlow()

    // --- Comment State ---
    private val _comments = MutableStateFlow<List<UserComment>>(emptyList())
    val comments: StateFlow<List<UserComment>> = _comments.asStateFlow()

    // ID bài học và User đang thao tác (Sẽ được gán khi loadLesson)
    private var currentLessonId: Int = -1
    private var currentUserId: Int = -1

    // --- LOAD BÀI HỌC THEO CATEGORY ---
    fun loadLessonsForCategory(categoryId: Int) {
        viewModelScope.launch {
            _lessons.value = repository.getLessonsByCategoryId(categoryId)
        }
    }

    // --- LOAD CHI TIẾT BÀI HỌC (QUIZ + AUDIO + COMMENT) ---
    // Cập nhật: Nhận thêm userId để biết ai đang làm bài
    fun loadLesson(lessonId: Int, userId: Int) {
        currentLessonId = lessonId
        currentUserId = userId
        Log.d("LessonViewModel", "Loading lesson: $lessonId for User: $userId")

        viewModelScope.launch {
            _isLoadingQuiz.value = true
            try {
                // 1. Lấy thông tin bài học (Audio)
                val lesson = repository.getLessonById(lessonId)
                _audioPath.value = lesson?.audioPath

                // 2. Lấy danh sách câu hỏi
                val questionList = repository.getQuestionsWithChoicesForLesson(lessonId)
                _questions.value = questionList

                // 3. Lắng nghe Comment từ Database (Real-time)
                launch {
                    repository.getCommentsForLesson(lessonId).collect { dbComments ->
                        val uiComments = dbComments.map { item ->
                            UserComment(
                                id = item.comment.commentId,
                                userName = item.fullName ?: item.username ?: "User ${item.comment.userId}",
                                content = item.comment.content,
                                timestamp = item.comment.createdAt ?: ""
                            )
                        }
                        _comments.value = uiComments
                    }
                }

            } catch (e: Exception) {
                Log.e("LessonViewModel", "Error loading lesson", e)
            } finally {
                _isLoadingQuiz.value = false
            }
        }
    }

    // --- GỬI COMMENT ---
    fun addComment(content: String) {
        if (currentLessonId == -1 || currentUserId == -1) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val currentDate = sdf.format(Date())

                val newComment = Comments(
                    userId = currentUserId, // Dùng ID thật
                    lessonId = currentLessonId,
                    content = content,
                    createdAt = currentDate
                )
                repository.addComment(newComment)
                Log.d("LessonViewModel", "Comment added successfully")
            } catch (e: Exception) {
                Log.e("LessonViewModel", "Insert comment failed", e)
            }
        }
    }

    // --- NỘP BÀI (TÍNH ĐIỂM + LƯU LỊCH SỬ) ---
    fun submitAnswers(selectedAnswers: Map<Int, String>) {
        var calculatedScore = 0
        val results = mutableListOf<AnswerResult>()

        // 1. Tính điểm logic
        _questions.value.forEach { questionWithChoices ->
            val question = questionWithChoices.question
            val userAnswer = selectedAnswers[question.questionId]?.trim() ?: ""

            // Xác định đáp án đúng tùy theo loại câu hỏi
            val correctAnswer: String = when (question.questionType) {
                "fill_in_the_blank" -> question.correctAnswer ?: ""
                else -> questionWithChoices.choices.find { it.isCorrect == 1 }?.choiceId?.toString() ?: ""
            }

            // So sánh
            val isCorrect = userAnswer.equals(correctAnswer, ignoreCase = true)
            if (isCorrect) calculatedScore++

            results.add(AnswerResult(question.questionId, userAnswer, correctAnswer, isCorrect))
        }

        // 2. Cập nhật State UI để hiển thị kết quả ngay lập tức
        _score.value = calculatedScore
        _quizResults.value = results

        // 3. --- LƯU VÀO DB ---
        if (currentUserId != -1 && currentLessonId != -1) {
            viewModelScope.launch {
                try {
                    // Lấy ngày hiện tại định dạng dd/MM (ví dụ: 25/11) để vẽ biểu đồ
                    val currentDate = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date())

                    val resultEntity = Results(
                        userId = currentUserId.toInt(),       // Chuyển từ String sang Int
                        lessonId = currentLessonId.toInt(),   // Nếu lessonId cũng là String
                        score = calculatedScore,
                        totalQuestions = _questions.value.size,
                        timeSpent = "00:00",                  // Hoặc thời gian thực
                        createdAt = currentDate
                    )

                    // Gọi Repository lưu xuống
                    repository.addResult(resultEntity)
                    Log.d("LessonViewModel", "Result saved: Score $calculatedScore")
                } catch (e: Exception) {
                    Log.e("LessonViewModel", "Error saving result", e)
                }
            }
        } else {
            Log.e("LessonViewModel", "Cannot save result: Missing UserId or LessonId")
        }
    }
}