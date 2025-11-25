package com.example.relisapp.nam.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.relisapp.nam.data.local.SessionManager
import com.example.relisapp.nam.di.ViewModelProviderFactory
import com.example.relisapp.nam.viewmodel.AuthViewModel
import com.example.relisapp.phat.ui.admin.CategoryListActivity
import com.example.relisapp.phat.ui.admin.LessonListActivity
import com.example.relisapp.phat.ui.admin.screen.BaseAdminScreen
import com.example.relisapp.phat.ui.theme.AdminProTheme
import com.example.relisapp.phat.ui.admin.screen.AdminDashboardScreenContent

class AdminDashboardActivity2 : ComponentActivity() {

    private lateinit var session: SessionManager
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ⭐ Session
        session = SessionManager(this)
        val userId = session.getUserId()

        if (userId == -1) {
            session.logout()
            navigateToStart()
            return
        }

        // ⭐ Inject ViewModel
        val factory = ViewModelProviderFactory.provideAuthViewModelFactory(this)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        setContent {
            AdminProTheme {

                // ⭐ State đúng chuẩn Compose
                var currentTab by remember { mutableStateOf(UserTab.HOME) }

                BaseAdminScreen (
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
                        startActivity(Intent(this, UserListActivity::class.java))
                    },
                    onFeedback = {
                        startActivity(Intent(this, AdminDashboardActivity::class.java))},
                    onIconUserClick = {
                        // MỞ TRANG PROFILE ADMIN
                        startActivity(Intent(this, ProfileActivity::class.java))
                    },

                    // ⭐ LOGOUT TRONG DRAWER
                    onLogout = {
                        handleLogout()   // ⬅️ GỌI LOGOUT THẬT
                    },

                ) { innerPadding ->

                    com.example.relisapp.phat.ui.admin.screen.AdminDashboardScreenContent (
                        modifier = Modifier,

                        onLogout = {
                            handleLogout()
                        },

                        onManageUsers = {
                            startActivity(Intent(this, UserListActivity::class.java))
                        },

                        onManageLC = {
                            startActivity(Intent(this, AdminDashboardActivity::class.java))
                        },
                        onManageCategories = {
                            startActivity(Intent(this, CategoryListActivity()::class.java))
                        },
                        onManageLessons = {
                            startActivity(Intent(this, LessonListActivity::class.java))
                        },
                    )
                }
            }
        }
    }

    private fun handleLogout() {
        // Xóa user trong ViewModel
        authViewModel.clearCurrentUser()

        // Xóa session
        session.logout()

        // Điều hướng về StartActivity (xoá entire history)
        navigateToStart()
    }

    private fun navigateToStart() {
        val intent = Intent(this, StartActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
