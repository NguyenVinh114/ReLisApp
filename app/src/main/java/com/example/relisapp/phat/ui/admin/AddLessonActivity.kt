// package com.example.relisapp.phat.ui.admin
package com.example.relisapp.phat.ui.admin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import com.example.relisapp.nam.ui.screens.user.ProfileActivity
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

                BaseAdminScreen(
                    title = "Add New Lesson",
                    onDashboard = {startActivity(Intent(this, AdminDashboardActivity::class.java))},
                    onManageCategories = {
                        startActivity(Intent(this, CategoryListActivity::class.java))
                        showToast(context, "Navigate to Manage Categories") },
                    onManageLessons = { showToast(context, "Navigate to Manage Lessons") },
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
                    // --- NỘI DUNG CHÍNH LÀ ADDLESSONSCREEN ---
                    AddLessonScreen(
                        modifier = modifierFromBase,
                        categories = categories,
                        onSave = { lesson ->
                            // [SỬA ĐỔI] Kích hoạt hàm addLesson trong ViewModel
                            lessonViewModel.addLesson(lesson)
                            Toast.makeText(context, "Lesson Saved!", Toast.LENGTH_SHORT).show()
                            finish()
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
