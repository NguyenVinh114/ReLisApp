// package com.example.relisapp.phat.ui.admin
package com.example.relisapp.phat.ui.admin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.error
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.relisapp.nam.ui.screens.ProfileActivity
import com.example.relisapp.phat.data.AppDatabase
import com.example.relisapp.phat.repository.CategoryRepository
import com.example.relisapp.phat.repository.LessonRepository
import com.example.relisapp.phat.ui.admin.screen.AddLessonScreen
import com.example.relisapp.phat.ui.admin.screen.BaseAdminScreen
import com.example.relisapp.phat.ui.theme.AdminProTheme
import com.example.relisapp.phat.viewmodel.CategoryViewModel
import com.example.relisapp.phat.viewmodel.LessonViewModel
import com.example.relisapp.phat.viewmodel.CategoryViewModelFactory
import com.example.relisapp.phat.viewmodel.LessonViewModelFactory
import com.example.relisapp.phat.viewmodel.SaveResult
import kotlinx.coroutines.flow.collectLatest


class AddLessonActivity : ComponentActivity() {

    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var lessonViewModel: LessonViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- Khởi tạo ViewModel ---
        val db = AppDatabase.getDatabase(this)
        val categoryRepo = CategoryRepository(db.categoryDao())
        val lessonRepo = LessonRepository(db.lessonDao())

        categoryViewModel = ViewModelProvider(this, CategoryViewModelFactory(categoryRepo))[CategoryViewModel::class.java]
        lessonViewModel = ViewModelProvider(this, LessonViewModelFactory(lessonRepo))[LessonViewModel::class.java]

        // Load dữ liệu cần thiết
        categoryViewModel.loadCategories()

        setContent {
            AdminProTheme {
                val categories by categoryViewModel.categories.collectAsState(initial = emptyList())
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
                    title = "Add New Lesson",
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
                    onManageUsers = { showToast(context, "Navigate to Manage Users") },
                    onFeedback = { showToast(context, "Navigate to Feedback") },
                    onLogout = {
                        showToast(context, "Logging out...")
                        finishAffinity()
                    },
                    onIconUserClick = {
                        startActivity(Intent(this, ProfileActivity::class.java))
                    }
                ) { modifierFromBase ->
                    AddLessonScreen(
                        modifier = modifierFromBase,
                        categories = categories,
                        onSave = { lesson ->
                            lessonViewModel.addLesson(lesson)
                        },
                        onBack = { finish() }
                    )
                }
            }
        }
    }

    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
