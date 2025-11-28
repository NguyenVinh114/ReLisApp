// In file: phat/ui/admin/screen/AddEditQuestionScreen.kt

package com.example.relisapp.phat.ui.admin.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.relisapp.phat.entity.model.QuestionType
import com.example.relisapp.phat.viewmodel.QuestionViewModel

// Data class để quản lý trạng thái của lựa chọn trên UI
data class ChoiceState(
    val id: Int, // Dùng để xác định choice trong list
    var text: String = "",
    var isCorrect: Boolean = false
)

@Composable
fun AddEditQuestionScreen(
    modifier: Modifier = Modifier,
    lessonId: Int,
    questionId: Int?, // Nhận questionId (có thể null)
    questionViewModel: QuestionViewModel,
    onSave: () -> Unit
) {
    // [ĐÚNG] Lấy dữ liệu từ thể hiện (instance) của ViewModel
    val questionDetails by questionViewModel.questionDetails.collectAsState()

    // --- State của UI ---
    var questionText by rememberSaveable { mutableStateOf("") }
    var questionType by remember { mutableStateOf(QuestionType.MULTIPLE_CHOICE) }
    val (choices, setChoices) = remember { mutableStateOf(listOf<ChoiceState>()) }
    var fillInBlankAnswer by rememberSaveable { mutableStateOf("") }
    var choiceCounter by rememberSaveable { mutableStateOf(0) }

    // [QUAN TRỌNG] Logic điền dữ liệu vào UI khi ở chế độ Sửa
    LaunchedEffect(questionDetails) {
        // Chỉ chạy khi questionDetails có dữ liệu (tức là ViewModel đã tải xong)
        questionDetails?.let { details ->
            questionText = details.question.questionText
            questionType = try {
                QuestionType.valueOf(details.question.questionType ?: "MULTIPLE_CHOICE")
            } catch (e: Exception) {
                QuestionType.MULTIPLE_CHOICE
            }

            if (questionType == QuestionType.MULTIPLE_CHOICE) {
                val newChoices = details.choices.mapIndexed { index, choice ->
                    ChoiceState(
                        id = index, // ID cho UI state, không phải ID trong DB
                        text = choice.choiceText,
                        isCorrect = choice.isCorrect == 1
                    )
                }
                setChoices(newChoices)
                choiceCounter = newChoices.size
            } else { // Fill in the blank
                fillInBlankAnswer = details.question.correctAnswer ?: ""
            }
        }
    }

    fun addChoice() {
        val newChoice = ChoiceState(id = choiceCounter)
        setChoices(choices + newChoice)
        choiceCounter++
    }

    // Chỉ thêm các lựa chọn mặc định khi ở chế độ THÊM MỚI
    LaunchedEffect(Unit) {
        // Nếu không ở chế độ sửa (questionId là null) và danh sách lựa chọn đang rỗng
        if (questionId == null && choices.isEmpty()) {
            addChoice()
            addChoice()
        }
    }

    val handleSave = {
        // Truyền `questionId` vào hàm save của ViewModel
        questionViewModel.saveQuestionData(
            lessonId = lessonId,
            questionId = questionId, // QUAN TRỌNG
            questionText = questionText,
            questionType = questionType,
            choicesState = choices,
            fillInBlankAnswer = fillInBlankAnswer
        )
    }

    // Xác định xem nút Save có được bật hay không
    val isSaveEnabled = when (questionType) {
        QuestionType.MULTIPLE_CHOICE -> questionText.isNotBlank() && choices.all { it.text.isNotBlank() } && choices.any { it.isCorrect }
        QuestionType.FILL_IN_THE_BLANK -> questionText.isNotBlank() && fillInBlankAnswer.isNotBlank()
    }

    // --- GIAO DIỆN (LAZYCOLUMN) ---
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Item nhập câu hỏi
        item {
            Text(
                text = if (questionId == null) "Add Question to Lesson $lessonId" else "Edit Question in Lesson $lessonId",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = questionText,
                onValueChange = { questionText = it },
                label = { Text("Question Text") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(Modifier.height(24.dp))
        }

        // Item chọn loại câu hỏi
        item {
            QuestionTypeSelector(
                selectedType = questionType,
                onTypeSelected = { questionType = it }
            )
            Spacer(Modifier.height(24.dp))
        }

        // Hiển thị giao diện tùy theo loại câu hỏi
        when (questionType) {
            QuestionType.MULTIPLE_CHOICE -> {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Choices",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Button(onClick = { addChoice() }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Choice")
                            Spacer(Modifier.width(4.dp))
                            Text("Add")
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
                itemsIndexed(choices, key = { _, choice -> choice.id }) { index, choice ->
                    ChoiceInputItem(
                        choiceIndex = index,
                        choiceState = choice,
                        onTextChange = { newText ->
                            val newList = choices.toMutableList()
                            newList[index] = choice.copy(text = newText)
                            setChoices(newList)
                        },
                        onCorrectChange = {
                            val newList = choices.map { it.copy(isCorrect = false) }.toMutableList()
                            newList[index] = newList[index].copy(isCorrect = true)
                            setChoices(newList)
                        },
                        onDelete = {
                            if (choices.size > 2) {
                                val newList = choices.toMutableList()
                                newList.removeAt(index)
                                setChoices(newList)
                            }
                        }
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
            QuestionType.FILL_IN_THE_BLANK -> {
                item {
                    Text(
                        text = "Correct Answer",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = fillInBlankAnswer,
                        onValueChange = { fillInBlankAnswer = it },
                        label = { Text("Enter the correct answer") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Tip: Use underscores `___` in the question text above to indicate the blank space.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }

        // Item nút lưu
        item {
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = handleSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = isSaveEnabled
            ) {
                Text("SAVE QUESTION", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun QuestionTypeSelector(
    selectedType: QuestionType,
    onTypeSelected: (QuestionType) -> Unit
) {
    Column {
        Text(
            text = "Question Type",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth()) {
            QuestionType.entries.forEach { type ->
                Row(
                    Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (selectedType == type) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            else Color.Transparent
                        )
                        .clickable { onTypeSelected(type) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    RadioButton(
                        selected = selectedType == type,
                        onClick = { onTypeSelected(type) }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = when(type) {
                            QuestionType.MULTIPLE_CHOICE -> "Multiple Choice"
                            QuestionType.FILL_IN_THE_BLANK -> "Fill in the Blank"
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ChoiceInputItem(
    choiceIndex: Int,
    choiceState: ChoiceState,
    onTextChange: (String) -> Unit,
    onCorrectChange: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onCorrectChange) {
            Icon(
                imageVector = if (choiceState.isCorrect) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircleOutline,
                contentDescription = "Mark as correct",
                tint = if (choiceState.isCorrect) MaterialTheme.colorScheme.primary else Color.Gray
            )
        }
        OutlinedTextField(
            value = choiceState.text,
            onValueChange = onTextChange,
            label = { Text("Choice ${choiceIndex + 1}") },
            modifier = Modifier.weight(1f),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = Color.Gray.copy(alpha = 0.5f)
            )
        )
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Choice",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}
