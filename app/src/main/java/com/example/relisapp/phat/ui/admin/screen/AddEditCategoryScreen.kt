package com.example.relisapp.phat.ui.admin.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
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
    // --- CÁC STATE CỦA UI ---
    // Khởi tạo các state này với giá trị rỗng hoặc mặc định ban đầu.
    var categoryName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Listening") }
    var expanded by remember { mutableStateOf(false) }

    val types = listOf("Listening", "Reading", "Grammar")
    val context = LocalContext.current
    val isEditing = existingCategory != null

    // --- [SỬA ĐỔI QUAN TRỌNG] ---
    // Sử dụng LaunchedEffect để "lắng nghe" sự thay đổi của `existingCategory`.
    // Khi `existingCategory` thay đổi từ `null` sang có dữ liệu (sau khi ViewModel tải xong),
    // khối lệnh này sẽ được thực thi để cập nhật state của UI.
    LaunchedEffect(existingCategory) {
        if (existingCategory != null) {
            categoryName = existingCategory.categoryName
            // Chuyển chữ cái đầu của type thành chữ hoa để khớp với danh sách dropdown
            selectedType = existingCategory.type.capitalize(Locale.current)
        }
    }

    // --- GIAO DIỆN ---
    // Giao diện bên dưới không cần thay đổi. Nó sẽ tự động "recompose" (vẽ lại)
    // khi các state `categoryName` và `selectedType` được cập nhật từ LaunchedEffect.
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
