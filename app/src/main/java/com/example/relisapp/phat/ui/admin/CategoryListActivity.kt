package com.example.relisapp.phat.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.relisapp.phat.data.AppDatabase
import com.example.relisapp.phat.repository.CategoryRepository
import com.example.relisapp.phat.viewmodel.CategoryViewModel
import com.example.relisapp.phat.viewmodel.CategoryViewModelFactory
import com.example.relisapp.ui.theme.ReLisAppTheme
import com.example.relisapp.phat.ui.admin.screen.*

class CategoryListActivity : ComponentActivity() {

    private lateinit var categoryViewModel: CategoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Khởi tạo ViewModel và Database
        val db = AppDatabase.getDatabase(this)
        val categoryRepo = CategoryRepository(db.categoryDao())
        categoryViewModel = ViewModelProvider(
            this,
            CategoryViewModelFactory(categoryRepo)
        )[CategoryViewModel::class.java]

        // Load dữ liệu
        categoryViewModel.loadCategories()

        setContent {
            ReLisAppTheme {
                val categories by categoryViewModel.categories.collectAsState(initial = emptyList())

                // --- TÍCH HỢP BASE ADMIN SCREEN ---
                BaseAdminScreen(
                    title = "Manage Categories", // Tiêu đề này khớp với highlight trong menu
                    onManageCategories = {
                        // Đang ở trang này rồi, có thể reload hoặc không làm gì
                        categoryViewModel.loadCategories()
                    },
                    onManageLessons = {
                        // Chuyển sang LessonActivity
                        // startActivity(Intent(this, LessonListActivity::class.java))
                        // finish() // Tùy chọn đóng activity hiện tại
                    },
                    onManageUsers = {
                        // Chuyển sang UserActivity
                        // startActivity(Intent(this, UserListActivity::class.java))
                    },
                    onFeedback = {
                        // Chuyển sang FeedbackActivity
                    },
                    onLogout = {
                        // Xử lý đăng xuất
                        finish()
                    }
                ) { modifierFromBase -> // paddingFromBase chứa khoảng cách an toàn tránh TopBar

                    // Gọi màn hình nội dung và truyền padding vào
                    CategoryListScreen(
                        categories = categories,
                        onAddClick = {
                            startActivity(Intent(this@CategoryListActivity, AddCategoryActivity::class.java))
                        },
                        modifier = modifierFromBase
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh dữ liệu khi quay lại từ màn hình Add
        categoryViewModel.loadCategories()
    }
}