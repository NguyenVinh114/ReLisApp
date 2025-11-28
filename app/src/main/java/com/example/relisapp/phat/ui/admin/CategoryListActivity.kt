// package com.example.relisapp.phat.ui.admin
package com.example.relisapp.phat.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.example.relisapp.nam.ui.screens.user.ProfileActivity
import com.example.relisapp.phat.data.AppDatabase
import com.example.relisapp.phat.repository.CategoryRepository
import com.example.relisapp.phat.ui.admin.screen.*
import com.example.relisapp.phat.ui.theme.AdminProTheme
import com.example.relisapp.phat.viewmodel.CategoryViewModel
import com.example.relisapp.phat.viewmodel.CategoryViewModelFactory

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

        setContent {
            AdminProTheme {
                val categories by categoryViewModel.categories.collectAsState(initial = emptyList())

                BaseAdminScreen(
                    title = "Manage Categories",
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
                    onManageUsers = { /* Chuyển sang UserActivity */ },
                    onFeedback = { /* Chuyển sang FeedbackActivity */ },
                    onLogout = { finish() },
                    onIconUserClick = {
                        startActivity(Intent(this, ProfileActivity::class.java))
                    }
                ) { modifierFromBase ->
                    CategoryListScreen(
                        categories = categories,
                        // [CẬP NHẬT] Truyền thẳng ViewModel vào Screen để xử lý sự kiện
                        viewModel = categoryViewModel,
                        onAddClick = {
                            // Chuyển sang màn hình AddEdit không có ID
                            startActivity(Intent(this@CategoryListActivity, AddEditCategoryActivity::class.java))
                        },
                        onEditClick = { category ->
                            // Chuyển sang màn hình AddEdit với ID của category
                            val intent = Intent(this@CategoryListActivity, AddEditCategoryActivity::class.java).apply {
                                putExtra("CATEGORY_ID", category.categoryId)
                            }
                            startActivity(intent)
                        },
                        onLockSuccess = {
                            Toast.makeText(this, "Category locked", Toast.LENGTH_SHORT).show()
                        },
                        modifier = modifierFromBase
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh dữ liệu khi quay lại từ màn hình thêm/sửa
        categoryViewModel.loadCategories()
    }
}
