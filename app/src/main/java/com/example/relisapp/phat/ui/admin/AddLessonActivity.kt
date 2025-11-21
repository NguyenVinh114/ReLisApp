package com.example.relisapp.phat.ui.admin

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext // Import LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.relisapp.phat.data.AppDatabase
import com.example.relisapp.phat.entity.Lessons
import com.example.relisapp.phat.entity.Categories
import com.example.relisapp.phat.repository.CategoryRepository
import com.example.relisapp.phat.repository.LessonRepository
import com.example.relisapp.phat.viewmodel.LessonViewModel
import com.example.relisapp.phat.viewmodel.CategoryViewModel
import com.example.relisapp.ui.theme.ReLisAppTheme


class CategoryViewModelFactory(private val repo: CategoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoryViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class LessonViewModelFactory(private val repo: LessonRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LessonViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LessonViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class AddLessonActivity : ComponentActivity() {

    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var lessonViewModel: LessonViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Khởi tạo database + repository
        val db = AppDatabase.getDatabase(this)
        val categoryRepo = CategoryRepository(db.categoryDao())
        val lessonRepo = LessonRepository(db.lessonDao())


        // Khởi tạo ViewModel
        categoryViewModel = ViewModelProvider(
            this,
            CategoryViewModelFactory(categoryRepo)
        )[CategoryViewModel::class.java]

        lessonViewModel = ViewModelProvider(
            this,
            LessonViewModelFactory(lessonRepo)
        )[LessonViewModel::class.java]

        // Load categories
        categoryViewModel.loadCategories()
        val types = listOf("Listening", "Reading", "Grammar")

        setContent {
            ReLisAppTheme {
                val categories by categoryViewModel.categories.collectAsState(initial = emptyList())
                val context = LocalContext.current

                AddLessonScreen(
                    categories = categories,
                    types = types,
                    onSave = { lesson ->
                        // Thêm logic lưu vào ViewModel ở đây
                        // lessonViewModel.addLesson(lesson)
                        Toast.makeText(context, "Lesson Saved!", Toast.LENGTH_SHORT).show()
                        finish() // Đóng activity sau khi lưu
                    },
                    onBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLessonScreen(
    categories: List<Categories>,
    types: List<String>,
    onSave: (Lessons) -> Unit,
    onBack: () -> Unit
) {
    var lesson by remember { mutableStateOf(Lessons(categoryId = 0, title = "", type = "")) }

    Column(modifier = Modifier.padding(16.dp)) {

        OutlinedTextField(
            value = lesson.title,
            onValueChange = { lesson = lesson.copy(title = it) },
            label = { Text("Title") }, // ✅ SỬA
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        // TYPE dropdown
        var typeExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = typeExpanded, onExpandedChange = { typeExpanded = it }) {
            OutlinedTextField(
                value = lesson.type,
                onValueChange = {},
                readOnly = true,
                label = { Text("Type") }, // ✅ SỬA
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                types.forEach { t ->
                    DropdownMenuItem(
                        text = { Text(t) },
                        onClick = {
                            lesson = lesson.copy(type = t)
                            typeExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // CATEGORY dropdown (lọc theo type)
        var catExpanded by remember { mutableStateOf(false) }
        val filteredCategories = remember(lesson.type, categories) {
            categories.filter { it.type == lesson.type }
        }

        ExposedDropdownMenuBox(expanded = catExpanded, onExpandedChange = { catExpanded = it }) {
            OutlinedTextField(
                value = filteredCategories.find { it.categoryId == lesson.categoryId }?.categoryName ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") }, // ✅ SỬA
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = catExpanded, onDismissRequest = { catExpanded = false }) {
                filteredCategories.forEach { c ->
                    DropdownMenuItem(
                        text = { Text(c.categoryName) },
                        onClick = {
                            lesson = lesson.copy(categoryId = c.categoryId)
                            catExpanded = false
                        }
                    )
                }
                if (filteredCategories.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("No categories available") }, // ✅ SỬA
                        onClick = { catExpanded = false }
                    )
                }
            }
        }


        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = lesson.content ?: "",
            onValueChange = { lesson = lesson.copy(content = it) },
            label = { Text("Content") }, // ✅ SỬA
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { onSave(lesson) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Lesson") // ✅ SỬA
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back") // ✅ SỬA
        }
    }
}
