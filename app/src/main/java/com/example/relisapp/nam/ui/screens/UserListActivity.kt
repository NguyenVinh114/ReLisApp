package com.example.relisapp.nam.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import com.example.relisapp.nam.database.AppDatabase
import com.example.relisapp.nam.database.entity.User
import com.example.relisapp.nam.ui.theme.LearnTheme
import com.example.relisapp.nam.data.repository.UserRepository
import com.example.relisapp.nam.data.local.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.relisapp.nam.utils.canDelete    // ⭐ IMPORT EXTENSION FUNCTION
import com.example.relisapp.nam.database.Converters.BitmapConverter


class UserListActivity : ComponentActivity() {

    private lateinit var repository: UserRepository
    private lateinit var sessionManager: SessionManager
    private var userList by mutableStateOf<List<User>>(emptyList())
    private var isRefreshing by mutableStateOf(false)
    private var currentUser: User? = null      // ⭐ Current user để check quyền

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize
        val database = AppDatabase.getDatabase(this)
        repository = UserRepository(database.userDao())
        sessionManager = SessionManager(this)

        // Tải current user trước (để check quyền)
        loadCurrentUser()

        // Load user list
        loadUsers()

        setContent {
            LearnTheme {
                UserListScreen(
                    users = userList,
                    onViewDetail = { user -> navigateToUserDetail(user) },
                    onToggleLock = { user -> toggleUserLock(user) },
                    onChangeRole = { user -> showChangeRoleDialog(user) },
                    onDeleteUser = { user -> deleteUser(user) },    // ⭐ CALLBACK XOÁ USER
                    onRefresh = { loadUsers() },
                    isRefreshing = isRefreshing,
                    currentUser = currentUser,// ⭐ TRUYỀN VÀO UI
                    onBack = { finish() }
                )
            }
        }
    }

    private fun loadCurrentUser() {
        lifecycleScope.launch {
            val userId = sessionManager.getUserId()
            if (userId != -1) {
                currentUser = repository.getUserById(userId)
            }
        }
    }

    private fun loadUsers() {
        isRefreshing = true
        lifecycleScope.launch {
            try {
                repository.getAllUsers().collect { users ->
                    userList = users
                    isRefreshing = false
                }
            } catch (e: Exception) {
                showToast("Lỗi tải danh sách: ${e.message}")
                isRefreshing = false
            }
        }
    }

    private fun navigateToUserDetail(user: User) {
        val intent = Intent(this, UserDetailActivity::class.java).apply {
            putExtra("USER_ID", user.userId)
            putExtra("USERNAME", user.username)
        }
        startActivity(intent)
    }

    private fun toggleUserLock(user: User) {
        lifecycleScope.launch {
            try {
                val currentUserId = sessionManager.getUserId()

                if (currentUserId == user.userId) {
                    showToast("⚠️ Không thể tự khóa tài khoản của bạn!")
                    return@launch
                }

                val newStatus = if (user.accountStatus == "locked") "active" else "locked"

                withContext(Dispatchers.IO) {
                    repository.updateUserStatus(user.userId, newStatus)
                }

                val message = if (newStatus == "locked") {
                    "🔒 Đã khóa tài khoản ${user.username}"
                } else {
                    "🔓 Đã mở khóa tài khoản ${user.username}"
                }
                showToast(message)

            } catch (e: Exception) {
                showToast("❌ Lỗi: ${e.message}")
            }
        }
    }

    private fun deleteUser(user: User) {
        val currentUserId = sessionManager.getUserId()

        // ⭐ Kiểm tra quyền xóa bằng extension canDelete()
        if (!currentUser.canDelete(user)) {
            Toast.makeText(this, "⛔ Bạn không có quyền xóa tài khoản này", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentUserId == user.userId) {
            Toast.makeText(this, "⚠️ Không thể tự xóa chính mình!", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            repository.deleteUserById(user.userId)
            Toast.makeText(this@UserListActivity, "🗑️ Đã xóa người dùng", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showChangeRoleDialog(user: User) {
        val intent = Intent(this, ChangeRoleActivity::class.java).apply {
            putExtra("USER_ID", user.userId)
            putExtra("CURRENT_ROLE", user.role)
        }
        startActivityForResult(intent, REQUEST_CHANGE_ROLE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHANGE_ROLE && resultCode == RESULT_OK) {
            // danh sách sẽ tự cập nhật bằng Flow → không cần reload
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUEST_CHANGE_ROLE = 1001
    }
}
