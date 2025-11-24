package com.example.relisapp.phat.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.relisapp.phat.ui.admin.screen.AdminDashboardScreenContent
import com.example.relisapp.phat.ui.admin.screen.BaseAdminScreen
import com.example.relisapp.phat.ui.admin.screen.CategoryListScreen
import com.example.relisapp.phat.ui.theme.AdminProTheme // <-- 1. IMPORT THEME MỚI

class AdminDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 2. SỬ DỤNG ADMINPROTHEME
            AdminProTheme {
                BaseAdminScreen(
                    title = "Admin Dashboard",
                    currentScreen = "Admin Dashboard", // Thêm để highlight menu
                    onDashboard = {},
                    onManageCategories = {
                        // Tạm thời trỏ đến AddCategory, nên đổi thành CategoryListActivity sau
                        startActivity(Intent(this, CategoryListActivity::class.java))
                    },
                    onManageLessons = {
                        startActivity(Intent(this, LessonListActivity::class.java))
                    },
                    onManageUsers = {
                        showToast("Navigate to Manage Users")
                    },
                    onFeedback = {
                        showToast("Navigate to Feedback")
                    },
                    onLogout = {
                        showToast("Logging out...")
                        finishAffinity() // Đóng ứng dụng
                    }
                ) { modifier ->
                    // 3. TRUYỀN TẤT CẢ CÁC HÀM VÀO CONTENT
                    AdminDashboardScreenContent(
                        modifier = modifier,
                        onManageCategories = {
                            startActivity(Intent(this, CategoryListActivity()::class.java))
                        },
                        onManageLessons = {
                            startActivity(Intent(this, LessonListActivity::class.java))
                        },
                        onManageUsers = {
                            showToast("Navigate to Manage Users")
                        },
                        onFeedback = {
                            showToast("Navigate to Feedback")
                        },
                        onLogout = {
                            showToast("Logging out...")
                            finishAffinity()
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
