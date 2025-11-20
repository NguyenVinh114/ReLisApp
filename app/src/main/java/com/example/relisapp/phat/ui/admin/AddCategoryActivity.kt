package com.example.relisapp.phat.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.relisapp.phat.data.AppDatabase
import com.example.relisapp.phat.entity.Categories
import com.example.relisapp.phat.repository.CategoryRepository
import com.example.relisapp.phat.viewmodel.CategoryViewModel
import com.example.relisapp.phat.viewmodel.CategoryViewModelFactory
import com.example.relisapp.ui.theme.ReLisAppTheme
import com.example.relisapp.phat.ui.admin.screen.*
class AddCategoryActivity : ComponentActivity() {

    private lateinit var categoryViewModel: CategoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(this)
        val categoryRepo = CategoryRepository(db.categoryDao())

        categoryViewModel = ViewModelProvider(
            this,
            CategoryViewModelFactory(categoryRepo)
        )[CategoryViewModel::class.java]

        setContent {
            ReLisAppTheme {

                // --- BỌC NỘI DUNG TRONG BASE ADMIN SCREEN ---
                BaseAdminScreen(
                    title = "Add New Category", // Tiêu đề TopBar
                    onManageCategories = { /* Chuyển trang Categories, hoặc reload */ },
                    onManageLessons = { /* Chuyển trang Lessons */ },
                    onManageUsers = { /* Chuyển trang Users */ },
                    onFeedback = { /* Chuyển trang Feedback */ },
                    onLogout = { finish() }
                ) { modifierFromBase -> // Nhận modifier đã có padding

                    AddCategoryScreen(
                        onSave = { category ->
                            categoryViewModel.addCategory(category)
                            Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        },
                        onBack = { finish() },
                        modifier = modifierFromBase // Truyền modifier để nội dung không bị che
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryScreen(
    onSave: (Categories) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier // Thêm tham số modifier
) {
    var categoryName by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf("Listening") } // default
    val types = listOf("Listening", "Reading")
    val context = LocalContext.current

    // Áp dụng modifier được truyền từ BaseAdminScreen
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp) // Chỉ thêm padding ngang, padding dọc đã có từ BaseScreen
    ) {
        // TopBar đã hiển thị tiêu đề, nên có thể bỏ qua Text này hoặc dùng cho tiêu đề nhỏ hơn
        // Text(
        //     text = "Add Category",
        //     style = MaterialTheme.typography.titleLarge
        // )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = categoryName,
            onValueChange = { categoryName = it },
            label = { Text("Category Name") }, // Dịch sang Tiếng Anh
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ---------------- Dropdown cho type ----------------
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Lesson Type") }, // Dịch sang Tiếng Anh
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null
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

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (categoryName.isBlank()) {
                    Toast.makeText(context, "Please enter category name", Toast.LENGTH_SHORT).show() // Dịch sang Tiếng Anh
                } else {
                    onSave(Categories(categoryName = categoryName, type = selectedType))
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Category") // Dịch sang Tiếng Anh
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back") // Dịch sang Tiếng Anh
        }
    }
}