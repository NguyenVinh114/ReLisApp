package com.example.relisapp.phat.ui.admin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.example.relisapp.nam.ui.screens.ProfileActivity
import com.example.relisapp.phat.data.AppDatabase
import com.example.relisapp.phat.repository.CategoryRepository
import com.example.relisapp.phat.repository.LessonRepository
import com.example.relisapp.phat.ui.admin.screen.BaseAdminScreen
import com.example.relisapp.phat.ui.admin.screen.LessonListScreen
import com.example.relisapp.phat.ui.theme.AdminProTheme
import com.example.relisapp.phat.ui.theme.UserFreshTheme
import com.example.relisapp.phat.viewmodel.CategoryViewModel
import com.example.relisapp.phat.viewmodel.CategoryViewModelFactory
import com.example.relisapp.phat.viewmodel.LessonViewModel
import com.example.relisapp.phat.viewmodel.LessonViewModelFactory

class LessonListActivity : ComponentActivity() {

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

        setContent {
            AdminProTheme {
                val lessons by lessonViewModel.lessons.collectAsState()
                val categories by categoryViewModel.categories.collectAsState()

                BaseAdminScreen(
                    title = "Manage Lessons",
                    currentScreen = "Manage Lessons",
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
                    onManageUsers = { showToast("Navigate to Manage Users") },
                    onFeedback = { showToast("Navigate to Feedback") },
                    onLogout = {
                        showToast("Logging out...")
                        finishAffinity()
                    },
                    onIconUserClick = {
                        startActivity(Intent(this, ProfileActivity::class.java))
                    }
                ) { modifier ->
                    LessonListScreen(
                        modifier = modifier,
                        lessons = lessons,
                        categories = categories,
                        viewModel = lessonViewModel,
                        onAddLesson = {
                            startActivity(Intent(this, AddLessonActivity::class.java))
                        },
                        onEditClick = { lessonId ->
                            showToast("Clicked lesson ID: $lessonId")
                            val intent = Intent(this, LessonDetailActivity::class.java)
                            intent.putExtra("LESSON_ID", lessonId)
                            startActivity(intent)
                        },
                        onSearch = lessonViewModel::searchLessons, // Truyền trực tiếp tham chiếu hàm
                        onFilter = lessonViewModel::filterLessons  // Truyền trực tiếp tham chiếu hàm
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
