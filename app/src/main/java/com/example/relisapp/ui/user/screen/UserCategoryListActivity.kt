package com.example.relisapp.ui.user.screen

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
import com.example.relisapp.data.local.data.AppDatabase
import com.example.relisapp.data.repository.CategoryRepository
import com.example.relisapp.ui.theme.UserFreshTheme
import com.example.relisapp.ui.viewmodel.CategoryViewModel
import com.example.relisapp.ui.viewmodel.CategoryViewModelFactory
import java.util.Locale

class UserCategoryListActivity : ComponentActivity() {

    private lateinit var categoryViewModel: CategoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. NHẬN DỮ LIỆU TỪ MAIN ACTIVITY
        // skillType sẽ là "listening" hoặc "reading" (hoặc null nếu vào từ tab khác)
        val skillType = intent.getStringExtra("TARGET_SKILL")
        val fromMain = intent.getStringExtra("from_main") ?: ""

        // Xử lý tiêu đề màn hình dựa trên skillType
        val screenTitle = when (skillType) {
            "listening" -> "Listening Practice" // Nếu là listening
            "reading" -> "Reading Practice"     // Nếu là reading
            else -> "All Topics"                // Mặc định
        }

        val db = AppDatabase.getDatabase(this)
        val repo = CategoryRepository(db.categoryDao())
        categoryViewModel = ViewModelProvider(
            this,
            CategoryViewModelFactory(repo)
        )[CategoryViewModel::class.java]

        categoryViewModel.loadCategories()

        setContent {
            UserFreshTheme {
                val categories by categoryViewModel.categories.collectAsState(initial = emptyList())

                BaseUserScreen(
                    // 2. GÁN TIÊU ĐỀ ĐÃ XỬ LÝ
                    title = screenTitle,
                    currentTab = UserTab.CATEGORIES,
                    onUserIconClick = {
                        Toast.makeText(this, "Open Profile Settings", Toast.LENGTH_SHORT).show()
                    },
                    onTabSelected = { selectedTab ->
                        when (selectedTab) {
                            UserTab.CATEGORIES -> { }
                            UserTab.HOME -> {
                                val intent = Intent(this, MainActivity::class.java)
                                // Xóa back stack để về hẳn Home
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                            UserTab.LESSON -> { Toast.makeText(this, "Go to My Lessons", Toast.LENGTH_SHORT).show() }
                            UserTab.PROFILE -> { Toast.makeText(this, "Go to Profile", Toast.LENGTH_SHORT).show() }
                        }
                    }
                ) { innerPadding ->

                    UserCategoryListScreen(
                        categories = categories,
                        fromMain = fromMain,
                        modifier = Modifier.padding(innerPadding),
                        onCategoryClick = { category ->

                            // 3. CHUYỂN SANG MÀN HÌNH DANH SÁCH BÀI HỌC (UserLessonListActivity)
                            val intent = Intent(this, UserLessonListActivity::class.java).apply {
                                putExtra("CATEGORY_ID", category.categoryId)
                                putExtra("CATEGORY_NAME", category.categoryName)

                                // QUAN TRỌNG: Truyền tiếp skillType ("listening" hoặc "reading")
                                // Để màn hình sau biết mà lọc bài học
                                if (skillType != null) {
                                    putExtra("SKILL_FILTER", skillType)
                                }
                            }
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}