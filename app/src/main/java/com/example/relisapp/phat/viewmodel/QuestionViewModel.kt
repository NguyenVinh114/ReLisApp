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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch


class QuestionViewModel(private val questionRepo: QuestionRepository) : ViewModel() {

    private val _questionsWithChoices = MutableStateFlow<List<QuestionWithChoices>>(emptyList())
    val questionsWithChoices: StateFlow<List<QuestionWithChoices>> = _questionsWithChoices.asStateFlow()

    private val _questionDetails = MutableStateFlow<QuestionWithChoices?>(null)
    val questionDetails: StateFlow<QuestionWithChoices?> = _questionDetails.asStateFlow()



    fun loadQuestionsForLesson(lessonId: Int) {
        viewModelScope.launch {
            questionRepo.getQuestionsWithChoicesForLesson(lessonId).collect { newList ->
                _questionsWithChoices.value = newList
            }
        }
    }

    fun loadQuestionDetails(questionId: Int) {
        // Tối ưu: không tải lại nếu đã có dữ liệu của câu hỏi đó
        if (_questionDetails.value?.question?.questionId == questionId) return

        viewModelScope.launch {
            // [SỬA] Chỉ lấy giá trị đầu tiên từ Flow
            val question = questionRepo.getQuestionDetails(questionId).firstOrNull()
            _questionDetails.value = question
        }
    }

    fun clearQuestionDetails() {
        _questionDetails.value = null
    }


    private val _saveResult = MutableSharedFlow<SaveResult>(replay = 0)
    val saveResult = _saveResult.asSharedFlow()
    fun saveQuestionData(
        lessonId: Int,
        questionId: Int?,
        questionText: String,
        questionType: QuestionType,
        choicesState: List<ChoiceState>,
        fillInBlankAnswer: String
    ) {
        viewModelScope.launch {
            try {
                val trimmedQuestionText = questionText.trim()
                val isDuplicate: Boolean

                if (questionId == null) {
                    isDuplicate = questionRepo.doesQuestionExist(trimmedQuestionText, lessonId)
                } else {
                    isDuplicate = questionRepo.doesQuestionExist(trimmedQuestionText, lessonId, questionId)
                }

                if (isDuplicate) {
                    _saveResult.emit(SaveResult.Existed("This exact question already exists in this lesson."))
                    return@launch
                }

                val (questionEntity, choicesEntities) = prepareEntities(
                    lessonId, questionId, trimmedQuestionText, questionType, choicesState, fillInBlankAnswer
                )

                if (questionId == null) {
                    questionRepo.saveQuestion(questionEntity, choicesEntities)
                } else {
                    questionRepo.updateQuestion(questionEntity, choicesEntities)
                }
                _saveResult.emit(SaveResult.Success)
            } catch (e: Exception) {
                _saveResult.emit(SaveResult.Failure(e))
            }
        }
    }


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
