package com.example.relisapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.relisapp.data.local.data.AppDatabase // Import cần thiết cho DB
import com.example.relisapp.phat.ui.user.UserCategoryListActivity // Import cho Category List
import com.example.relisapp.ui.favorite.FavoriteScreen
import com.example.relisapp.ui.feedback.FeedbackScreen
import com.example.relisapp.ui.listening.ListeningScreen
import com.example.relisapp.ui.search.SearchScreen
import com.example.relisapp.ui.theme.ReLisAppTheme
import com.example.relisapp.ui.user.screen.HomeScreen // Cần đảm bảo import chính xác
import com.example.relisapp.ui.user.screen.ProgressScreen // Cần đảm bảo import chính xác
import com.example.relisapp.ui.user.screen.UserQuizActivity // Cần đảm bảo import chính xác
import com.example.relisapp.ui.viewmodel.HomeViewModel
import com.example.relisapp.ui.viewmodel.ProgressViewModel
// Các import khác từ phiên bản 1 đã được giữ lại nếu cần:
// import com.example.relisapp.ui.progress.ProgressScreen // -> thay thế bằng .ui.user.screen.ProgressScreen
// import com.example.relisapp.ui.screens.HomeScreen // -> thay thế bằng .ui.user.screen.HomeScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Khởi tạo Database
        val db = AppDatabase.getDatabase(this)

        // 2. Khởi tạo HomeViewModel
        val homeViewModel: HomeViewModel by viewModels {
            HomeViewModel.Factory(
                lessonDao = db.lessonDao(),
                favoriteLessonDao = db.favoriteLessonDao(),
                userDao = db.userDao()
            )
        }

        // 3. Khởi tạo ProgressViewModel
        val progressViewModel: ProgressViewModel by viewModels {
            ProgressViewModel.Factory(resultDao = db.resultDao())
        }

        // 4. Lấy User ID từ SharedPreferences
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val currentUserId = sharedPref.getInt("USER_ID", 6) // default là 6

        setContent {
            ReLisAppTheme {
                val currentScreen = remember { mutableStateOf("home") }

                when (currentScreen.value) {

                    "home" -> HomeScreen(
                        homeViewModel = homeViewModel, // ✅ Thêm ViewModel
                        onListeningClick = {
                            // Chuyển sang Category List cho Listening
                            val intent = Intent(this@MainActivity, UserCategoryListActivity::class.java).apply {
                                putExtra("TARGET_SKILL", "listening")
                                putExtra("from_main", "Listening")
                            }
                            startActivity(intent)
                        },
                        onReadingClick = {
                            // Chuyển sang Category List cho Reading
                            val intent = Intent(this@MainActivity, UserCategoryListActivity::class.java).apply {
                                putExtra("TARGET_SKILL", "reading")
                                putExtra("from_main", "Reading")
                            }
                            startActivity(intent)
                        },
                        onProgressClick = {
                            currentScreen.value = "progress"
                        },
                        onSearchClick = { currentScreen.value = "search" },
                        onFavoriteClick = { currentScreen.value = "favorite" }
                    )

                    "progress" -> ProgressScreen(
                        viewModel = progressViewModel, // ✅ Thêm ViewModel
                        userId = currentUserId, // ✅ truyền UserId vào
                        onBack = { currentScreen.value = "home" }
                    )

                    "listening" -> ListeningScreen(
                        onBack = { currentScreen.value = "home" }
                    )

                    "search" -> SearchScreen(
                        onBack = { currentScreen.value = "home" },
                        onLessonClick = { lesson ->
                            // Chuyển sang màn hình Feedback/Làm bài sau khi chọn bài
                            currentScreen.value = "feedback"
                        }
                    )

                    "feedback" -> FeedbackScreen(
                        onBack = { currentScreen.value = "home" },
                        lessonTitle = "Listening – Travel"
                    )

                    "reading" -> {
                        // TODO: ReadingScreen sẽ thêm sau
                    }

                    "favorite" -> FavoriteScreen(
                        homeViewModel = homeViewModel, // ✅ Thêm ViewModel
                        onBack = { currentScreen.value = "home" },
                        onLessonClick = { lesson ->
                            // Chuyển sang màn hình làm bài (UserQuizActivity)
                            val intent = Intent(this@MainActivity, UserQuizActivity::class.java).apply {
                                putExtra("LESSON_ID", lesson.lessonId)
                                putExtra("LESSON_TITLE", lesson.title)
                            }
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}