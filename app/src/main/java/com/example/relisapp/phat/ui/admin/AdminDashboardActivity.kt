package com.example.relisapp.phat.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.relisapp.phat.ui.admin.screen.AdminDashboardScreenContent
import com.example.relisapp.phat.ui.admin.screen.BaseAdminScreen

class AdminDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                BaseAdminScreen(
                    title = "Admin Dashboard",
                    onManageCategories = {
                        startActivity(Intent(this, CategoryListActivity::class.java))
                    },
                    onManageLessons = {
                        startActivity(Intent(this, AddLessonActivity::class.java))
                    },
                    onManageUsers = {
                        Toast.makeText(this, "Manage Users clicked", Toast.LENGTH_SHORT).show()
                    },
                    onFeedback = {
                        Toast.makeText(this, "Feedback clicked", Toast.LENGTH_SHORT).show()
                    },
                    onLogout = {
                        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
                    }
                ) { modifier ->
                    AdminDashboardScreenContent(
                        modifier,
                        onManageCategories = {
                            startActivity(Intent(this, CategoryListActivity::class.java))
                        },
                        onManageLessons = {},
                        onManageUsers = {},
                        onFeedback = {},
                        )
                }
            }
        }
    }
}
