package com.example.relisapp.phat.ui.admin

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
                AddLessonScreen(
                    categories = categories,
                    types = types,
                    onSave = { lesson ->
                        lesson.createdBy = 1  // giả định adminId
                        lessonViewModel.addLesson(lesson)
                        Toast.makeText(this, "Thêm bài học thành công", Toast.LENGTH_SHORT).show()
                        finish()
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
    var lessons by remember { mutableStateOf(Lessons(categoryId = 0, title = "", type = "")) }

    Column(modifier = Modifier.padding(16.dp)) {

        OutlinedTextField(
            value = lessons.title,
            onValueChange = { lessons = lessons.copy(title = it) },
            label = { Text("Tiêu đề") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        // TYPE dropdown
        var typeExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = typeExpanded, onExpandedChange = { typeExpanded = it }) {
            OutlinedTextField(
                value = lessons.type,
                onValueChange = {},
                readOnly = true,
                label = { Text("Loại bài") },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                types.forEach { t ->
                    DropdownMenuItem(
                        text = { Text(t) },
                        onClick = {
                            lessons = lessons.copy(type = t)
                            typeExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // CATEGORY dropdown (lọc theo type)
        var catExpanded by remember { mutableStateOf(false) }
        val filteredCategories = remember(lessons.type, categories) {
            categories.filter { it.type == lessons.type }
        }

        ExposedDropdownMenuBox(expanded = catExpanded, onExpandedChange = { catExpanded = it }) {
            OutlinedTextField(
                value = filteredCategories.find { it.categoryId == lessons.categoryId }?.categoryName ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Chủ đề") },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = catExpanded, onDismissRequest = { catExpanded = false }) {
                filteredCategories.forEach { c ->
                    DropdownMenuItem(
                        text = { Text(c.categoryName) },
                        onClick = {
                            lessons = lessons.copy(categoryId = c.categoryId)
                            catExpanded = false
                        }
                    )
                }
                if (filteredCategories.isEmpty()) {
                    DropdownMenuItem(text = { Text("Không có chủ đề") }, onClick = { catExpanded = false })
                }
            }
        }


        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = lessons.content ?: "",
            onValueChange = { lessons = lessons.copy(content = it) },
            label = { Text("Nội dung") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { onSave(lessons) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Lưu bài học")
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Quay lại")
        }
    }
}
