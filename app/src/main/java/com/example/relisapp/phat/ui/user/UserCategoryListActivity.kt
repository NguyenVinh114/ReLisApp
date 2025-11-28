package com.example.relisapp.phat.ui.user

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.relisapp.MainActivity
import com.example.relisapp.phat.data.AppDatabase
import com.example.relisapp.phat.repository.CategoryRepository
import com.example.relisapp.phat.ui.user.screen.BaseUserScreen
import com.example.relisapp.phat.ui.user.screen.UserCategoryListScreen
import com.example.relisapp.phat.ui.user.screen.UserTab
import com.example.relisapp.phat.viewmodel.CategoryViewModel
import com.example.relisapp.phat.viewmodel.CategoryViewModelFactory

// IMPORT THEME MỚI
import com.example.relisapp.phat.ui.theme.UserFreshTheme

class UserCategoryListActivity : ComponentActivity() {

    private lateinit var categoryViewModel: CategoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fromMain = intent.getStringExtra("from_main") ?: ""

        // 1. Khởi tạo ViewModel
        val db = AppDatabase.getDatabase(this)
        val repo = CategoryRepository(db.categoryDao())
        categoryViewModel = ViewModelProvider(
            this,
            CategoryViewModelFactory(repo)
        )[CategoryViewModel::class.java]

        categoryViewModel.loadCategories()

        setContent {
            // 2. SỬ DỤNG THEME RIÊNG (UserFreshTheme)
            UserFreshTheme {
                val categories by categoryViewModel.categoriesForUser.collectAsState(initial = emptyList())

                BaseUserScreen(
                    title = "All Topics", // Đổi tên title cho thân thiện người học
                    currentTab = UserTab.CATEGORIES,

                    // Xử lý Click User Icon (Góc phải)
                    onUserIconClick = {
                        Toast.makeText(this, "Open Profile Settings", Toast.LENGTH_SHORT).show()
                        // Code: startActivity(Intent(this, ProfileActivity::class.java))
                    },

                    // Xử lý Click Navigation
                    onTabSelected = { selectedTab ->
                        when (selectedTab) {
                            UserTab.CATEGORIES -> { /* Đang ở đây */ }
                            UserTab.HOME -> {
                                Toast.makeText(this, "Back to Home", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            }
                            UserTab.LESSON -> {
                                Toast.makeText(this, "Go to My Lessons", Toast.LENGTH_SHORT).show()
                            }
                            UserTab.PROFILE -> {
                                Toast.makeText(this, "Go to Profile", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                ) { innerPadding ->

                    // 3. HIỂN THỊ DANH SÁCH
                    UserCategoryListScreen(
                        categories = categories,
                        fromMain = fromMain,
                        modifier = Modifier.padding(innerPadding),
                        onCategoryClick = { category ->
                            // SỬA ĐOẠN CODE NÀY
                            // Toast.makeText(this, "Start learning: ${category.categoryName}", Toast.LENGTH_SHORT).show()

                            // Chuyển sang UserLessonListActivity và truyền dữ liệu cần thiết
                            val intent = Intent(this, UserLessonListActivity::class.java).apply {
                                putExtra("CATEGORY_ID", category.categoryId)
                                putExtra("CATEGORY_NAME", category.categoryName)
                            }
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}