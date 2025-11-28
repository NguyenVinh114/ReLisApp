package com.example.relisapp.phat.ui.admin.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.relisapp.phat.entity.Categories
import com.example.relisapp.phat.entity.Lessons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLessonScreen(
    // [SỬA ĐỔI 1/2] Nhận Modifier từ màn hình cha
    modifier: Modifier = Modifier,
    categories: List<Categories>,
    onSave: (Lessons) -> Unit,
    onBack: () -> Unit
) {
    // --- State cho các trường dữ liệu của Lesson (Giữ nguyên) ---
    var title by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Listening") }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
    var selectedLevel by remember { mutableStateOf("A1") }
    var content by remember { mutableStateOf("") }
    var audioPath by remember { mutableStateOf("") }
    var transcript by remember { mutableStateOf("") }

    // --- State cho UI (Giữ nguyên) ---
    val context = LocalContext.current
    val types = listOf("Listening", "Reading")
    val levels = listOf("A1", "A2", "B1","B2","C1","C2")

    // Lọc danh sách category dựa trên Type đã chọn (Giữ nguyên)
    val filteredCategories = remember(selectedType, categories) {
        categories.filter { it.type.equals(selectedType, ignoreCase = true) }
    }

    // Tự động reset categoryId khi type thay đổi (Giữ nguyên)
    LaunchedEffect(selectedType) {
        selectedCategoryId = null
    }

    // --- Bố cục giao diện ---
    Column(
        // [SỬA ĐỔI 2/2] Áp dụng modifier từ cha và thêm padding ngang
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp) // <-- THÊM PADDING NGANG Ở ĐÂY
            .verticalScroll(rememberScrollState()), // Cho phép cuộn nếu nội dung dài
        verticalArrangement = Arrangement.spacedBy(16.dp) // Dùng spacedBy cho khoảng cách đều
    ) {
        // Thêm một khoảng trống ở trên cùng để giao diện thoáng hơn
        Spacer(Modifier.height(8.dp))

        // --- CÁC TRƯỜNG NHẬP LIỆU ---

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Lesson Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // --- Dropdown cho Type ---
        DropdownField(
            label = "Type",
            options = types,
            selectedValue = selectedType,
            onValueChange = { selectedType = it }
        )

        // --- Dropdown cho Category (lọc theo Type) ---
        val categoryName = filteredCategories.find { it.categoryId == selectedCategoryId }?.categoryName ?: ""
        DropdownField(
            label = "Category",
            options = filteredCategories.map { it.categoryName },
            selectedValue = categoryName,
            onValueChange = { name ->
                selectedCategoryId = filteredCategories.find { it.categoryName == name }?.categoryId
            },
            enabled = filteredCategories.isNotEmpty()
        )
        if (filteredCategories.isEmpty()) {
            Text(
                text = "No categories available for this type. Please add one first.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }

        // --- Dropdown cho Level ---
        DropdownField(
            label = "Level",
            options = levels,
            selectedValue = selectedLevel,
            onValueChange = { selectedLevel = it }
        )

        // --- Các trường Text còn lại ---
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

        // --- Đẩy các nút xuống dưới cùng ---
        Spacer(Modifier.weight(1f))

        // --- CÁC NÚT HÀNH ĐỘNG ---
        Button(
            onClick = {
                if (title.isBlank() || selectedCategoryId == null) {
                    Toast.makeText(context, "Title and Category are required", Toast.LENGTH_SHORT).show()
                } else {
                    val newLesson = Lessons(
                        categoryId = selectedCategoryId!!,
                        title = title,
                        type = selectedType.lowercase(),
                        level = selectedLevel.lowercase(),
                        content = content.ifBlank { null },
                        audioPath = audioPath.ifBlank { null },
                        transcript = transcript.ifBlank { null }
                    )
                    onSave(newLesson)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ){ Text("Save Lesson")}

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ){Text("Back")}

        // Thêm một khoảng đệm nhỏ ở dưới cùng để nút không dính sát đáy màn hình khi cuộn
        Spacer(Modifier.height(8.dp))
    }
}

/**
 * Composable tái sử dụng cho các trường Dropdown (ExposedDropdownMenuBox)
 * (Hàm này giữ nguyên, không thay đổi)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    label: String,
    options: List<String>,
    selectedValue: String,
    onValueChange: (String) -> Unit,
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
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            enabled = enabled
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
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
