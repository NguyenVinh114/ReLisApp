package com.example.relisapp.phat.ui.user

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.relisapp.MainActivity
import com.example.relisapp.phat.data.AppDatabase
import com.example.relisapp.phat.repository.LessonRepository
import com.example.relisapp.phat.ui.theme.UserFreshTheme
import com.example.relisapp.phat.ui.user.screen.BaseUserScreen
import com.example.relisapp.phat.ui.user.screen.UserLessonListScreen
import com.example.relisapp.phat.ui.user.screen.UserTab
import com.example.relisapp.phat.viewmodel.LessonViewModel
import com.example.relisapp.phat.viewmodel.LessonViewModelFactory

class UserLessonListActivity : ComponentActivity() {

    private lateinit var lessonViewModel: LessonViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Lấy categoryId và categoryName được truyền từ Activity trước
        val categoryId = intent.getIntExtra("CATEGORY_ID", -1)
        val categoryName = intent.getStringExtra("CATEGORY_NAME") ?: "Lessons"

        if (categoryId == -1) {
            Toast.makeText(this, "Error: Category not found.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Khởi tạo ViewModel
        val db = AppDatabase.getDatabase(this)
        val repo = LessonRepository(db.lessonDao()) // Sử dụng LessonDao
        lessonViewModel = ViewModelProvider(this, LessonViewModelFactory(repo))[LessonViewModel::class.java]

        // Yêu cầu ViewModel tải dữ liệu
        lessonViewModel.loadLessonsForCategory(categoryId)

        setContent {
            UserFreshTheme {
                val lessons by lessonViewModel.lessons.collectAsStateWithLifecycle()

                BaseUserScreen(
                    title = categoryName,
                    currentTab = UserTab.CATEGORIES,
                    onUserIconClick = { },
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
                    UserLessonListScreen(
                        lessons = lessons,
                        modifier = Modifier.padding(innerPadding),
                        onLessonClick = { lesson ->
                            // SỬA ĐOẠN NÀY: Thay vì chỉ hiển thị Toast
                            // Toast.makeText(this, "Opening lesson: ${lesson.title}", Toast.LENGTH_SHORT).show()

                            // 1. Tạo Intent để mở UserQuizActivity
                            val intent = Intent(this, UserQuizActivity::class.java)

                            // 2. Đặt dữ liệu lessonId vào Intent
                            intent.putExtra("LESSON_ID", lesson.lessonId)
                            intent.putExtra("LESSON_TITLE",lesson.title)

                            // 3. Khởi chạy Activity mới
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}
