package com.example.relisapp.phat.ui.admin.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue// package com.example.relisapp.phat.ui.admin.screen
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import com.example.relisapp.phat.entity.Categories

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCategoryScreen(
    // Category để chỉnh sửa, null nếu là thêm mới
    existingCategory: Categories?,
    onSave: (Categories) -> Unit,
    onDelete: ((Categories) -> Unit)? = null, // Hàm xóa, chỉ hiển thị nút xóa khi được cung cấp
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var categoryName by remember { mutableStateOf(existingCategory?.categoryName ?: "") }
    var expanded by remember { mutableStateOf(false) }
    // Khởi tạo type từ category có sẵn, hoặc mặc định
    var selectedType by remember {
        mutableStateOf(existingCategory?.type?.capitalize(Locale.current) ?: "Listening")
    }
    val types = listOf("Listening", "Reading", "Grammar")
    val context = LocalContext.current
    val isEditing = existingCategory != null

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = categoryName,
            onValueChange = { categoryName = it },
            label = { Text("Category Name") },
            modifier = Modifier.fillMaxWidth()
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Lesson Type") },
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, "Dropdown") },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                types.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            selectedType = type
                            expanded = false
                        }
                    )
                }
            }
        }

        Button(
            onClick = {
                if (categoryName.isBlank()) {
                    Toast.makeText(context, "Please enter a category name", Toast.LENGTH_SHORT).show()
                } else {
                    val categoryToSave = Categories(
                        categoryId = existingCategory?.categoryId ?: 0, // 0 cho thêm mới, Room sẽ tự tăng
                        categoryName = categoryName,
                        type = selectedType.lowercase() // Luôn lưu chữ thường
                    )
                    onSave(categoryToSave)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isEditing) "Update Category" else "Save Category")
        }

        // Chỉ hiển thị nút Xóa nếu đang ở chế độ chỉnh sửa và có hàm onDelete
        if (isEditing && onDelete != null) {
            Button(
                onClick = { onDelete(existingCategory!!) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Delete Category")
            }
        }

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCategoryScreen(
    onSave: (Categories) -> Unit,
    onBack: () -> Unit,
    // [SỬA ĐỔI 1/2] Nhận modifier từ layout cha (BaseAdminScreen)
    modifier: Modifier = Modifier
) {
    var categoryName by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf("Listening") } // Giá trị mặc định
    val types = listOf("Listening", "Reading", "Grammar")
    val context = LocalContext.current

    // [SỬA ĐỔI 2/2] Áp dụng modifier từ cha và thêm padding ngang
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp), // <-- THÊM PADDING NGANG Ở ĐÂY
        verticalArrangement = Arrangement.spacedBy(16.dp) // Dùng spacedBy cho khoảng cách đều
    ) {
        // Spacer ở đầu không còn cần thiết nếu BaseAdminScreen đã có TopAppBar
        // Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = categoryName,
            onValueChange = { categoryName = it },
            label = { Text("Category Name") },
            modifier = Modifier.fillMaxWidth()
        )

        // Spacer đã được thay thế bằng Arrangement.spacedBy

        // --- Dropdown cho Type ---
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Lesson Type") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Dropdown"
                    )
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                types.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            selectedType = type
                            expanded = false
                        }
                    )
                }
            }
        }

        // Spacer đã được thay thế bằng Arrangement.spacedBy

        Button(
            onClick = {
                if (categoryName.isBlank()) {
                    Toast.makeText(context, "Please enter a category name", Toast.LENGTH_SHORT).show()
                } else {
                    // Chuyển type về chữ thường khi lưu để thống nhất dữ liệu
                    onSave(Categories(categoryName = categoryName, type = selectedType.lowercase()))
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Category")
        }

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}
