package com.example.relisapp.phat.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.launch
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.relisapp.nam.ui.screens.ProfileActivity
import com.example.relisapp.phat.data.AppDatabase
import com.example.relisapp.phat.repository.CategoryRepository
import com.example.relisapp.phat.ui.admin.screen.AddEditCategoryScreen
import com.example.relisapp.phat.ui.admin.screen.BaseAdminScreen
import com.example.relisapp.phat.ui.theme.AdminProTheme
import com.example.relisapp.phat.viewmodel.CategoryViewModel
import com.example.relisapp.phat.viewmodel.CategoryViewModelFactory
import com.example.relisapp.phat.viewmodel.SaveResult
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddEditCategoryActivity : ComponentActivity() {

    private lateinit var categoryViewModel: CategoryViewModel
    private var categoryId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        categoryId = intent.getIntExtra("CATEGORY_ID", -1)
        val isEditing = categoryId != -1

        val db = AppDatabase.getDatabase(this)
        val categoryRepo = CategoryRepository(db.categoryDao())
        categoryViewModel = ViewModelProvider(this, CategoryViewModelFactory(categoryRepo))[CategoryViewModel::class.java]

        setContent {
            AdminProTheme {
                val categoryToEdit by categoryViewModel.currentCategory.collectAsState()
                val context = LocalContext.current
                LaunchedEffect(key1 = true) {
                    categoryViewModel.saveResult.collectLatest { result ->
                        when (result) {
                            is SaveResult.Success -> {
                                Toast.makeText(context, "Lesson Saved!", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            is SaveResult.Existed -> {
                                Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                            }
                            is SaveResult.Failure -> {
                                Toast.makeText(context, "An unexpected error occurred: ${result.error.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }


                LaunchedEffect(key1 = categoryId) {
                    if (isEditing) {
                        categoryViewModel.loadCategoryById(categoryId)
                    } else {
                        categoryViewModel.clearCurrentCategory()
                    }
                }

                BaseAdminScreen(
                    title = if (isEditing) "Edit Category" else "Add New Category",
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
                ) { modifierFromBase ->
                    AddEditCategoryScreen(
                        modifier = modifierFromBase,
                        existingCategory = categoryToEdit,
                        onSave = {categoryToSave ->
                            if (isEditing) {
                                categoryViewModel.updateCategory(categoryToSave)
                            } else {
                                categoryViewModel.addCategory(categoryToSave)
                            }
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