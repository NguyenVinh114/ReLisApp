package com.example.relisapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.relisapp.ui.favorite.FavoriteScreen
import com.example.relisapp.ui.feedback.FeedbackScreen
import com.example.relisapp.ui.screens.HomeScreen
import com.example.relisapp.ui.listening.ListeningScreen
import com.example.relisapp.ui.progress.ProgressScreen
import com.example.relisapp.ui.search.SearchScreen
import com.example.relisapp.ui.theme.ReLisAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReLisAppTheme {
                val currentScreen = remember { mutableStateOf("home") }

                when (currentScreen.value) {
                    "home" -> HomeScreen(
                        onListeningClick = { currentScreen.value = "listening" },
                        onReadingClick = { currentScreen.value = "reading" },
                        onProgressClick = { currentScreen.value = "progress" },   // ✅ thêm Progress
                        onSearchClick = { currentScreen.value = "search" },
                        onFavoriteClick = { currentScreen.value = "favorite" }    // ✅ thêm Favorite
                    )

                    "listening" -> ListeningScreen(
                        onBack = { currentScreen.value = "home" }
                    )

                    "search" -> SearchScreen(
                        onBack = { currentScreen.value = "home" },
                        onLessonClick = { lesson ->
                            // 👉 Ví dụ: mở Feedback sau khi chọn bài
                            currentScreen.value = "feedback"
                        }
                    )
                    "feedback" -> FeedbackScreen(
                        onBack = { currentScreen.value = "home" },
                        lessonTitle = "Listening – Travel" // sau này truyền dynamic nếu muốn
                    )
                    "progress" -> ProgressScreen(   // ✅ màn tiến bộ
                        onBack = { currentScreen.value = "home" }
                    )

                    "favorite" -> FavoriteScreen(   // ✅ màn yêu thích
                        onBack = { currentScreen.value = "home" }
                    )

                    "reading" -> {
                        // TODO: ReadingScreen sẽ thêm sau
                    }
                }

            }
        }
    }
}
