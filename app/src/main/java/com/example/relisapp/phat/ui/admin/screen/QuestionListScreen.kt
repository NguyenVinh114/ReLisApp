// In file: phat/ui/admin/screen/QuestionListScreen.kt
package com.example.relisapp.phat.ui.admin.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.relisapp.phat.entity.Choices
import com.example.relisapp.phat.entity.model.QuestionType
import com.example.relisapp.phat.entity.model.QuestionWithChoices

@Composable
fun QuestionListScreen(
    modifier: Modifier = Modifier,
    lessonId: Int,
    lessonContent: String?, // <-- NHẬN CONTENT (có thể null)
    questionsWithChoices: List<QuestionWithChoices>,
    onAddNewQuestion: () -> Unit,
    onDeleteQuestion: (QuestionWithChoices) -> Unit,
    onEditQuestion: (QuestionWithChoices) -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddNewQuestion,
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Question") },
                text = { Text("Add Question") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- [THÊM MỚI] HIỂN THỊ NỘI DUNG BÀI HỌC ---
            if (!lessonContent.isNullOrBlank()) {
                item {
                    Column {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Lesson Content",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = lessonContent,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider()
                    }
                }
            }
            // ---------------------------------------------

            // Hiển thị phần câu hỏi (thêm tiêu đề)
            item {
                Text(
                    text = "Questions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            if (questionsWithChoices.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No questions found for this lesson.\nTap '+' to add one.",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            } else {
                items(items = questionsWithChoices, key = { it.question.questionId }) { item ->
                    QuestionItem(
                        questionWithChoices = item,
                        onEdit = { onEditQuestion(item) },
                        onDelete = { onDeleteQuestion(item) }
                    )
                }
            }

            item { Spacer(Modifier.height(80.dp)) } // Padding cho FAB
        }
    }
}

// Các Composable QuestionItem và ChoiceItem giữ nguyên
// ...
@Composable
fun QuestionItem(
    questionWithChoices: QuestionWithChoices,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val question = questionWithChoices.question
    val questionType = try {
        QuestionType.valueOf(question.questionType?.uppercase() ?: "MULTIPLE_CHOICE")
    } catch (e: IllegalArgumentException) {
        QuestionType.MULTIPLE_CHOICE
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = question.questionText,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f),
                    maxLines = 3
                )
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Question")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            when (questionType) {
                QuestionType.MULTIPLE_CHOICE -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (questionWithChoices.choices.isNotEmpty()) {
                            questionWithChoices.choices.forEach { choice ->
                                ChoiceItem(choice = choice)
                            }
                        } else {
                            Text(
                                text = "This multiple choice question has no choices.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                QuestionType.FILL_IN_THE_BLANK -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Subtitles,
                            contentDescription = "Correct Answer",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Answer: ",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = question.correctAnswer ?: "N/A",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChoiceItem(choice: Choices) {
    val isCorrect = choice.isCorrect == 1
    val backgroundColor = if (isCorrect) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    }
    val contentColor = if (isCorrect) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, shape = MaterialTheme.shapes.small)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isCorrect) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
            contentDescription = if (isCorrect) "Correct Answer" else "Incorrect Answer",
            tint = contentColor.copy(alpha = if (isCorrect) 1.0f else 0.7f),
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = choice.choiceText,
            color = contentColor,
            fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal
        )
    }
}
