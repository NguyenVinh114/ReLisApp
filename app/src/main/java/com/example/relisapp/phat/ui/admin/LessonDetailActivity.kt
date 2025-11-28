package com.example.relisapp.phat.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import com.example.relisapp.nam.ui.screens.ProfileActivity
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
import com.example.relisapp.phat.viewmodel.SaveResult
import kotlinx.coroutines.flow.collectLatest

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
                val context = LocalContext.current

                LaunchedEffect(Unit) {
                    lessonViewModel.saveResult.collectLatest { result ->
                        when (result) {
                            is SaveResult.Success -> {
                                Toast.makeText(context, "Lesson Saved!", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            is SaveResult.Existed -> {
                                Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                            }
                            is SaveResult.Failure -> {
                                Toast.makeText(context, "An unexpected error occurred: ${result.error.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
                BaseAdminScreen(
                    title = "Lesson Details",
                    onDashboard = { startActivity(Intent(this, AdminDashboardActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }) },
                    onManageCategories = { startActivity(Intent(this, CategoryListActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }) },
                    onManageLessons = {
                        startActivity(Intent(this, LessonListActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        })
                    },
                    onManageUsers = { /* ... */ },
                    onFeedback = { /* ... */ },
                    onLogout = { /* ... */ },
                    onIconUserClick = {
                        startActivity(Intent(this, ProfileActivity::class.java))
                    }
                ) { modifier ->
                    LessonDetailScreen(
                        modifier = modifier,
                        lesson = lesson,
                        categories = categories,
                        onUpdate = { updatedLesson ->
                            lessonViewModel.updateLesson(updatedLesson)
                        },
                        onBack = { finish() },
                        onNavigateToQuestions = { lessonId ->
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
