// File: com/example/relisapp/phat/ui/user/UserQuizActivity.kt
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
import com.example.relisapp.phat.repository.QuestionRepository
import com.example.relisapp.phat.ui.theme.UserFreshTheme
import com.example.relisapp.phat.ui.user.screen.BaseUserScreen // <-- IMPORT
import com.example.relisapp.phat.ui.user.screen.UserQuizScreen
import com.example.relisapp.phat.ui.user.screen.UserTab         // <-- IMPORT
import com.example.relisapp.phat.viewmodel.QuizViewModel
import com.example.relisapp.phat.viewmodel.QuizViewModelFactory

class UserQuizActivity : ComponentActivity() {

    private lateinit var quizViewModel: QuizViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val lessonId = intent.getIntExtra("LESSON_ID", -1)
        val lessonTitle = intent.getStringExtra("LESSON_TITLE") ?: "Quiz"
        if (lessonId == -1) {
            Toast.makeText(this, "Error: Lesson not found.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Khởi tạo ViewModel (giữ nguyên)
        val db = AppDatabase.getDatabase(this)
        val lessonRepo = LessonRepository(db.lessonDao())
        val questionRepo = QuestionRepository(db.questionDao())
        val factory = QuizViewModelFactory(lessonRepo, questionRepo)
        quizViewModel = ViewModelProvider(this, factory)[QuizViewModel::class.java]

        quizViewModel.loadQuizData(lessonId)

        setContent {
            UserFreshTheme {
                // Thu thập tất cả state từ ViewModel (giữ nguyên)
                val questions by quizViewModel.questions.collectAsStateWithLifecycle()
                val score by quizViewModel.score.collectAsStateWithLifecycle()
                val isLoading by quizViewModel.isLoadingQuiz.collectAsStateWithLifecycle()
                val audioPath by quizViewModel.audioPath.collectAsStateWithLifecycle()
                val lessonContent by quizViewModel.lessonContent.collectAsStateWithLifecycle()
                val quizResults by quizViewModel.quizResults.collectAsStateWithLifecycle()

                // [SỬA] BỌC TOÀN BỘ GIAO DIỆN TRONG BASEUSERSCREEN
                BaseUserScreen(
                    title = lessonTitle,
                    currentTab = UserTab.LESSON, // Hoặc một tab phù hợp khác
                    onUserIconClick = {
                        Toast.makeText(this, "Profile Clicked", Toast.LENGTH_SHORT).show()
                    },
                    onTabSelected = { selectedTab ->
                        // Xử lý điều hướng khi người dùng nhấn tab ở Bottom Bar hoặc Drawer
                        when (selectedTab) {
                            UserTab.CATEGORIES -> {
                                // Điều hướng về màn hình danh sách Category
                                val intent = Intent(this, UserCategoryListActivity::class.java) // Giả sử tên là UserCategoryListActivity
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                startActivity(intent)
                            }
                            UserTab.HOME -> {
                                // Điều hướng về màn hình Home chính
                                val intent = Intent(this, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                startActivity(intent)
                            }
                            else -> Toast.makeText(this, "Navigating to ${selectedTab.title}", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) { innerPadding -> // `content` lambda của BaseUserScreen
                    UserQuizScreen(
                        modifier = Modifier.padding(innerPadding), // Áp dụng padding từ BaseUserScreen
                        // lessonTitle không còn cần thiết vì đã có ở TopBar
                        //lessonTitle = null,
                        questionsWithChoices = questions,
                        isLoading = isLoading,
                        audioPath = audioPath,
                        lessonContent = lessonContent,
                        score = score,
                        quizResults = quizResults,
                        onSubmit = { selectedAnswers ->
                            quizViewModel.submitAnswers(selectedAnswers)
                        }
                    )
                }
            }
        }
    }
}
