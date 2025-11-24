package com.example.relisapp.phat.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.example.relisapp.phat.data.AppDatabase
import com.example.relisapp.phat.repository.CategoryRepository
import com.example.relisapp.phat.ui.admin.screen.AddEditCategoryScreen
import com.example.relisapp.phat.ui.admin.screen.BaseAdminScreen
import com.example.relisapp.phat.ui.theme.AdminProTheme
import com.example.relisapp.phat.viewmodel.CategoryViewModel
import com.example.relisapp.phat.viewmodel.CategoryViewModelFactory

class AddEditCategoryActivity : ComponentActivity() {

    private lateinit var categoryViewModel: CategoryViewModel
    private var categoryId: Int = -1 // -1 nghĩa là thêm mới

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Lấy ID từ Intent. Nếu không có, giá trị mặc định là -1
        categoryId = intent.getIntExtra("CATEGORY_ID", -1)
        val isEditing = categoryId != -1

        // --- Khởi tạo ViewModel ---
        val db = AppDatabase.getDatabase(this)
        val categoryRepo = CategoryRepository(db.categoryDao())
        categoryViewModel = ViewModelProvider(
            this,
            CategoryViewModelFactory(categoryRepo)
        )[CategoryViewModel::class.java]

        setContent {
            AdminProTheme {
                // Lấy category hiện tại từ ViewModel
                val categoryToEdit by categoryViewModel.currentCategory.collectAsState()

                // Tải dữ liệu category nếu đang ở chế độ chỉnh sửa
                LaunchedEffect(key1 = categoryId) {
                    if (isEditing) {
                        categoryViewModel.loadCategoryById(categoryId)
                    } else {
                        categoryViewModel.clearCurrentCategory()
                    }
                }

                BaseAdminScreen(
                    title = if (isEditing) "Edit Category" else "Add New Category",
                    onDashboard = {startActivity(Intent(this, AdminDashboardActivity::class.java))},
                    onManageCategories = { finish() },
                    onManageLessons = { startActivity(Intent(this, LessonListActivity::class.java)) },
                    onManageUsers = { showToast("Navigate to Manage Users") },
                    onFeedback = { showToast("Navigate to Feedback") },
                    onLogout = {
                        showToast("Logging out...")
                        finishAffinity()
                    }
                ) { modifierFromBase ->
                    // Sử dụng màn hình AddEditCategoryScreen
                    AddEditCategoryScreen(
                        modifier = modifierFromBase,
                        existingCategory = categoryToEdit,
                        onSave = { category ->
                            if (isEditing) {
                                categoryViewModel.updateCategory(category)
                                showToast("Category updated successfully")
                            } else {
                                categoryViewModel.addCategory(category)
                                showToast("Category added successfully")
                            }
                            finish()
                        },
                        onDelete = { category ->
                            categoryViewModel.deleteCategory(category)
                            showToast("Category deleted successfully")
                            finish()
                        },
                        onBack = {
                            finish()
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
