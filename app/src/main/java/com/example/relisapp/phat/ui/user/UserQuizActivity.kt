// File: com/example/relisapp/phat/ui/user/UserQuizActivity.kt
package com.example.relisapp.phat.ui.user

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
// ⭐ [QUAN TRỌNG] Thêm dòng này để sửa lỗi "has no method getValue"
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.relisapp.MainActivity
// 1. Import Database của module Phat
import com.example.relisapp.phat.data.AppDatabase
// 2. Import Database và logic của module Nam (Dùng alias để tránh trùng tên)
import com.example.relisapp.nam.database.AppDatabase as NamDatabase
import com.example.relisapp.nam.data.repository.UserRepository
import com.example.relisapp.nam.logic.StreakManager
import com.example.relisapp.nam.data.local.SessionManager

import com.example.relisapp.phat.repository.LessonRepository
import com.example.relisapp.phat.repository.QuestionRepository
import com.example.relisapp.phat.ui.theme.UserFreshTheme
import com.example.relisapp.phat.ui.user.screen.BaseUserScreen
import com.example.relisapp.phat.ui.user.screen.UserQuizScreen
import com.example.relisapp.phat.ui.user.screen.UserTab
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

        // --- KHỞI TẠO CÁC DEPENDENCY ---

        // 1. Module Phat (Lesson, Question)
        val dbPhat = AppDatabase.getDatabase(this)
        val lessonRepo = LessonRepository(dbPhat.lessonDao())
        val questionRepo = QuestionRepository(dbPhat.questionDao())

        // 2. Module Nam (Streak, User Session)
        val dbNam = NamDatabase.getDatabase(this) // Database chứa bảng Users và Streak
        val sessionManager = SessionManager(this)

        // Tạo Repo và Manager cho Streak
        val userRepo = UserRepository(dbNam.userDao())
        val streakManager = StreakManager(dbNam.studySessionDao(), userRepo)

        // 3. Khởi tạo ViewModelFactory với ĐỦ 4 THAM SỐ
        val factory = QuizViewModelFactory(
            lessonRepo,
            questionRepo,
            streakManager, // Tham số mới 1
            sessionManager // Tham số mới 2
        )

        quizViewModel = ViewModelProvider(this, factory)[QuizViewModel::class.java]
        quizViewModel.loadQuizData(lessonId)

        setContent {
            UserFreshTheme {
                // Nhờ có import androidx.compose.runtime.getValue ở trên, từ khóa 'by' sẽ hoạt động
                val questions by quizViewModel.questions.collectAsStateWithLifecycle()
                val score by quizViewModel.score.collectAsStateWithLifecycle()
                val isLoading by quizViewModel.isLoadingQuiz.collectAsStateWithLifecycle()
                val audioPath by quizViewModel.audioPath.collectAsStateWithLifecycle()
                val lessonContent by quizViewModel.lessonContent.collectAsStateWithLifecycle()
                val quizResults by quizViewModel.quizResults.collectAsStateWithLifecycle()

                BaseUserScreen(
                    title = lessonTitle,
                    currentTab = UserTab.LESSON,
                    onUserIconClick = {
                        // Xử lý click profile
                    },
                    onTabSelected = { selectedTab ->
                        when (selectedTab) {
                            UserTab.CATEGORIES -> {
                                val intent = Intent(this, UserCategoryListActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                startActivity(intent)
                            }
                            UserTab.HOME -> {
                                val intent = Intent(this, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                startActivity(intent)
                            }
                            else -> Toast.makeText(this, "Navigating to ${selectedTab.title}", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) { innerPadding ->
                    UserQuizScreen(
                        modifier = Modifier.padding(innerPadding),
                        questionsWithChoices = questions,
                        isLoading = isLoading,
                        audioPath = audioPath,
                        lessonContent = lessonContent,
                        score = score,
                        quizResults = quizResults,
                        onSubmit = { selectedAnswers ->
                            // Gọi hàm submit (đã bao gồm logic tính điểm và streak)
                            quizViewModel.submitAnswers(selectedAnswers)
                        }
                    )
                }
            }
        }
    }
}