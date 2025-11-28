package com.example.relisapp.phat.ui.admin.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Quiz
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.relisapp.phat.entity.Categories
import com.example.relisapp.phat.entity.Lessons

@Composable
fun LessonDetailScreen(
    modifier: Modifier = Modifier,
    lesson: Lessons?,
    categories: List<Categories>,
    onUpdate: (Lessons) -> Unit,
    onBack: () -> Unit,
    onNavigateToQuestions: (Int) -> Unit
) {
    val context = LocalContext.current

    if (lesson == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // --- Các state giữ nguyên ---
    var title by remember(lesson) { mutableStateOf(lesson.title) }
    var selectedType by remember(lesson) { mutableStateOf(lesson.type.replaceFirstChar { it.titlecase() }) }
    var selectedCategoryId by remember(lesson) { mutableStateOf<Int?>(lesson.categoryId) }
    var selectedLevel by remember(lesson) { mutableStateOf(lesson.level?.replaceFirstChar { it.titlecase() } ?: "Beginner") }
    var content by remember(lesson) { mutableStateOf(lesson.content ?: "") }
    var audioPath by remember(lesson) { mutableStateOf(lesson.audioPath ?: "") }
    var transcript by remember(lesson) { mutableStateOf(lesson.transcript ?: "") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val types = listOf("Listening", "Reading")
    val levels = listOf("A1", "A2", "B1","B2","C1","C2")
    val availableCategoriesForType = categories.filter { it.type.equals(selectedType, ignoreCase = true) }

    SideEffect {
        val isCurrentCategoryValid = availableCategoriesForType.any { it.categoryId == selectedCategoryId }
        if (!isCurrentCategoryValid) {
            if (!selectedType.equals(lesson.type, ignoreCase = true)) {
                selectedCategoryId = null
            } else {
                selectedCategoryId = lesson.categoryId
            }
        }
    }

    // --- Bố cục giao diện ---
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        // --- Các trường nhập liệu giữ nguyên ---
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Lesson Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        GenericDropdownField(
            label = "Type",
            options = types,
            selectedValue = selectedType,
            onOptionSelected = { selectedType = it },
            getDisplayName = { it }
        )
        GenericDropdownField(
            label = "Category",
            options = availableCategoriesForType,
            selectedValue = availableCategoriesForType.find { it.categoryId == selectedCategoryId }?.categoryName ?: "",
            onOptionSelected = { category -> selectedCategoryId = category.categoryId },
            getDisplayName = { it.categoryName },
            enabled = availableCategoriesForType.isNotEmpty()
        )
        GenericDropdownField(
            label = "Level",
            options = levels,
            selectedValue = selectedLevel,
            onOptionSelected = { selectedLevel = it },
            getDisplayName = { it }
        )
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Content") },
            modifier = Modifier.fillMaxWidth().height(150.dp)
        )
        OutlinedTextField(
            value = audioPath,
            onValueChange = { audioPath = it },
            label = { Text("Audio Path (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = transcript,
            onValueChange = { transcript = it },
            label = { Text("Transcript (Optional)") },
            modifier = Modifier.fillMaxWidth().height(120.dp)
        )

        // [THÊM MỚI 2/2] Nút điều hướng sang màn hình quản lý câu hỏi
        Spacer(Modifier.height(8.dp))
        Card(
            onClick = { onNavigateToQuestions(lesson.lessonId) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Quiz,
                    contentDescription = "Questions",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Manage Questions",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
        Spacer(Modifier.height(8.dp))


        Spacer(Modifier.weight(1f))

        // --- Các nút hành động Update, Delete, Back giữ nguyên ---
        Button(
            onClick = {
                if (title.isBlank() || selectedCategoryId == null) {
                    Toast.makeText(context, "Title and Category are required", Toast.LENGTH_SHORT).show()
                } else {
                    val updatedLesson = lesson.copy(
                        categoryId = selectedCategoryId!!,
                        title = title,
                        type = selectedType.lowercase(),
                        level = selectedLevel.lowercase(),
                        content = content.ifBlank { null },
                        audioPath = audioPath.ifBlank { null },
                        transcript = transcript.ifBlank { null }
                    )
                    onUpdate(updatedLesson)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) { Text("Update Lesson") }

        OutlinedButton(
            onClick = { showDeleteDialog = true },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) { Text("Delete Lesson") }

        TextButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Back to List") }

        Spacer(Modifier.height(8.dp))
    }

    /*// --- Dialog xác nhận xóa giữ nguyên ---
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete the lesson '${lesson.title}'? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete(lesson)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }*/
}

/**
 * Composable tái sử dụng cho các trường Dropdown, làm việc với danh sách đối tượng (generic).
 * (Hàm này không thay đổi)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> GenericDropdownField(
    label: String,
    options: List<T>,
    selectedValue: String,
    onOptionSelected: (T) -> Unit,
    getDisplayName: (T) -> String,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            enabled = enabled
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(getDisplayName(option)) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
            if (options.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No options available") },
                    onClick = { expanded = false },
                    enabled = false
                )
            }
        }
    }
}
