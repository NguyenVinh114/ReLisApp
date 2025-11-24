package com.example.relisapp.phat.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.example.relisapp.phat.data.AppDatabase
import com.example.relisapp.phat.repository.CategoryRepository
import com.example.relisapp.phat.repository.LessonRepository
import com.example.relisapp.phat.ui.admin.screen.BaseAdminScreen
import com.example.relisapp.phat.ui.admin.screen.LessonDetailScreen
import com.example.relisapp.phat.ui.theme.AdminProTheme
import com.example.relisapp.phat.viewmodel.CategoryViewModel
import com.example.relisapp.phat.viewmodel.CategoryViewModelFactory
import com.example.relisapp.phat.viewmodel.LessonViewModel
import com.example.relisapp.phat.viewmodel.LessonViewModelFactory

class LessonDetailActivity : ComponentActivity() {

    private val lessonViewModel: LessonViewModel by lazy {
        val factory = LessonViewModelFactory(LessonRepository(AppDatabase.getDatabase(this).lessonDao()))
        ViewModelProvider(this, factory)[LessonViewModel::class.java]
    }

    private val categoryViewModel: CategoryViewModel by lazy {
        val factory = CategoryViewModelFactory(CategoryRepository(AppDatabase.getDatabase(this).categoryDao()))
        ViewModelProvider(this, factory)[CategoryViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val lessonId = intent.getIntExtra("LESSON_ID", -1)
        if (lessonId == -1) {
            showToast("Error: Invalid Lesson ID")
            finish()
            return
        }

        // Tải dữ liệu cần thiết
        lessonViewModel.loadLessonDetails(lessonId)
        categoryViewModel.loadCategories() // Cần cho dropdown

        setContent {
            AdminProTheme {
                val lesson by lessonViewModel.lessonDetails.collectAsState()
                val categories by categoryViewModel.categories.collectAsState()

                BaseAdminScreen(
                    title = "Lesson Details",
                    onDashboard = { /* Điều hướng */ },
                    onManageCategories = { /* Điều hướng */ },
                    onManageLessons = {
                        // Quay lại màn hình danh sách
                        startActivity(Intent(this, LessonListActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        })
                    },
                    onManageUsers = { /* ... */ },
                    onFeedback = { /* ... */ },
                    onLogout = { /* ... */ }
                ) { modifier ->
                    LessonDetailScreen(
                        modifier = modifier,
                        lesson = lesson,
                        categories = categories,
                        onUpdate = { updatedLesson ->
                            lessonViewModel.updateLesson(updatedLesson)
                            showToast("Lesson updated successfully!")
                            finish() // Quay lại màn hình trước đó
                        },
                        onDelete = { lessonToDelete ->
                            lessonViewModel.deleteLesson(lessonToDelete)
                            showToast("Lesson deleted.")
                            finish() // Quay lại màn hình danh sách
                        },
                        onBack = { finish() }, // Đơn giản là đóng activity hiện tại
                        onNavigateToQuestions = { lessonId ->
                            // [THAY ĐỔI Ở ĐÂY]
                            val intent = Intent(this, QuestionListActivity::class.java).apply {
                                putExtra("LESSON_ID", lessonId)
                            }
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
