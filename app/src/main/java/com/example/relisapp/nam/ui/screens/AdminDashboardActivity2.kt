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
            MaterialTheme {

                // ⭐ State đúng chuẩn Compose
                var currentTab by remember { mutableStateOf(UserTab.HOME) }

                BaseUserScreen(
                    title = "Admin Dashboard",

                    currentTab = currentTab,

                    onTabSelected = { selected ->
                        currentTab = selected
                    },

                    onUserIconClick = {
                        // MỞ TRANG PROFILE ADMIN
                        startActivity(Intent(this, ProfileActivity::class.java))
                    },

                    // ⭐ LOGOUT TRONG DRAWER
                    onLogout = {
                        handleLogout()   // ⬅️ GỌI LOGOUT THẬT
                    }
                ) { innerPadding ->

                    AdminDashboardScreenContent(
                        modifier = Modifier.padding(innerPadding),

                        onLogout = {
                            handleLogout()
                        },

                        onManageUsers = {
                            startActivity(Intent(this, UserListActivity::class.java))
                        },

                        onManageLC = {
                            startActivity(Intent(this, AdminDashboardActivity::class.java))
                        }
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
