// In file: phat/viewmodel/QuestionViewModel.kt

package com.example.relisapp.phat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.phat.entity.Choices
import com.example.relisapp.phat.entity.Questions
import com.example.relisapp.phat.entity.model.QuestionType
import com.example.relisapp.phat.entity.model.QuestionWithChoices
import com.example.relisapp.phat.repository.QuestionRepository
import com.example.relisapp.phat.ui.admin.screen.ChoiceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * ViewModel này CHỈ chịu trách nhiệm cho các nghiệp vụ liên quan đến QUESTION.
 */
class QuestionViewModel(private val questionRepo: QuestionRepository) : ViewModel() {

    // --- STATE CHO MÀN HÌNH DANH SÁCH CÂU HỎI ---
    private val _questionsWithChoices = MutableStateFlow<List<QuestionWithChoices>>(emptyList())
    val questionsWithChoices: StateFlow<List<QuestionWithChoices>> = _questionsWithChoices.asStateFlow()

    // --- STATE CHO MÀN HÌNH SỬA CÂU HỎI ---
    private val _questionDetails = MutableStateFlow<QuestionWithChoices?>(null)
    val questionDetails: StateFlow<QuestionWithChoices?> = _questionDetails.asStateFlow()


    // --- CÁC HÀM XỬ LÝ LOGIC CHO QUESTION ---

    /**
     * Tải danh sách câu hỏi (kèm lựa chọn) cho một bài học cụ thể.
     */
    fun loadQuestionsForLesson(lessonId: Int) {
        viewModelScope.launch {
            questionRepo.getQuestionsWithChoicesForLesson(lessonId).collect { newList ->
                _questionsWithChoices.value = newList
            }
        }
    }

    /**
     * Tải chi tiết một câu hỏi để chuẩn bị cho việc chỉnh sửa.
     */
    fun loadQuestionDetails(questionId: Int) {
        // Tối ưu: không tải lại nếu đã có dữ liệu của câu hỏi đó
        if (_questionDetails.value?.question?.questionId == questionId) return

        viewModelScope.launch {
            // [SỬA] Chỉ lấy giá trị đầu tiên từ Flow
            val question = questionRepo.getQuestionDetails(questionId).firstOrNull()
            _questionDetails.value = question
        }
    }
    /**
     * Dọn dẹp state chi tiết câu hỏi, dùng khi thoát màn hình sửa hoặc chuyển sang thêm mới.
     */
    fun clearQuestionDetails() {
        _questionDetails.value = null
    }

    /**
     * Lưu dữ liệu câu hỏi (thêm mới hoặc cập nhật) vào cơ sở dữ liệu.
     */
    fun saveQuestionData(
        lessonId: Int,
        questionId: Int?,
        questionText: String,
        questionType: QuestionType,
        choicesState: List<ChoiceState>,
        fillInBlankAnswer: String
    ) {
        viewModelScope.launch {
            val (questionEntity, choicesEntities) = prepareEntities(
                lessonId, questionId, questionText, questionType, choicesState, fillInBlankAnswer
            )

            if (questionId == null) {
                // Chế độ Thêm mới
                questionRepo.saveQuestion(questionEntity, choicesEntities)
            } else {
                // Chế độ Sửa
                questionRepo.updateQuestion(questionEntity, choicesEntities)
            }
        }
    }

    /**
     * Hàm helper để chuyển đổi dữ liệu từ UI state sang các đối tượng Entity.
     */
    private fun prepareEntities(
        lessonId: Int,
        questionId: Int?,
        questionText: String,
        questionType: QuestionType,
        choicesState: List<ChoiceState>,
        fillInBlankAnswer: String
    ): Pair<Questions, List<Choices>> {
        val question: Questions
        val choices: List<Choices>

        if (questionType == QuestionType.MULTIPLE_CHOICE) {
            question = Questions(
                questionId = questionId ?: 0,
                lessonId = lessonId,
                questionText = questionText,
                questionType = questionType.name.lowercase(),
                correctAnswer = null
            )
            choices = choicesState.map { state ->
                Choices(
                    choiceId = 0,
                    questionId = questionId ?: 0,
                    choiceText = state.text,
                    isCorrect = if (state.isCorrect) 1 else 0
                )
            }
        } else { // FILL_IN_THE_BLANK
            question = Questions(
                questionId = questionId ?: 0,
                lessonId = lessonId,
                questionText = questionText,
                questionType = questionType.name.lowercase(),
                correctAnswer = fillInBlankAnswer
            )
            choices = emptyList()
        }
        return Pair(question, choices)
    }

    fun deleteQuestion(question: Questions) {
        viewModelScope.launch {
            questionRepo.deleteQuestion(question) // Giả sử repository có hàm này
        }
    }
}
