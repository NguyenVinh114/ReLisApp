package com.example.relisapp.ui.user.screen

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.relisapp.data.local.data.AppDatabase
import com.example.relisapp.data.repository.LessonRepository
import com.example.relisapp.ui.theme.UserFreshTheme
import com.example.relisapp.ui.viewmodel.LessonViewModel
import com.example.relisapp.ui.viewmodel.LessonViewModelFactory

class UserQuizActivity : ComponentActivity() {

    private lateinit var lessonViewModel: LessonViewModel
    // Không cần CommentViewModel nữa vì LessonViewModel đã xử lý hết

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Lấy Lesson ID và Title từ Intent
        val lessonId = intent.getIntExtra("LESSON_ID", -1)
        val lessonTitle = intent.getStringExtra("LESSON_TITLE")

        // 2. Lấy User ID từ SharedPreferences
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val currentUserId = sharedPref.getInt("USER_ID", 6) // Mặc định là 1

        if (lessonId == -1) {
            Toast.makeText(this, "Error: Lesson not found.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 3. Khởi tạo Database & Repository
        val db = AppDatabase.getDatabase(this)

        // QUAN TRỌNG: Truyền cả lessonDao và resultDao vào Repository
        val repo = LessonRepository(db.lessonDao(), db.resultDao())

        // 4. Khởi tạo ViewModel
        lessonViewModel = ViewModelProvider(this, LessonViewModelFactory(repo))[LessonViewModel::class.java]

        // 5. Load dữ liệu (Truyền cả lessonId và userId)
        lessonViewModel.loadLesson(lessonId, currentUserId)

        setContent {
            UserFreshTheme {
                // Lấy các State từ LessonViewModel
                val questions by lessonViewModel.questions.collectAsStateWithLifecycle()
                val score by lessonViewModel.score.collectAsStateWithLifecycle()
                val isLoading by lessonViewModel.isLoadingQuiz.collectAsStateWithLifecycle()
                val audioPath by lessonViewModel.audioPath.collectAsStateWithLifecycle()
                val quizResults by lessonViewModel.quizResults.collectAsStateWithLifecycle()

                // Lấy comment trực tiếp từ LessonViewModel (Nó đã được map sang UserComment rồi)
                val comments by lessonViewModel.comments.collectAsStateWithLifecycle()

                UserQuizScreen(
                    lessonTitle = lessonTitle,
                    questionsWithChoices = questions,
                    isLoading = isLoading,
                    audioPath = audioPath,
                    score = score,
                    quizResults = quizResults,

                    // --- PHẦN COMMENT ---
                    comments = comments,
                    onAddComment = { content ->
                        // Gọi hàm addComment của LessonViewModel
                        lessonViewModel.addComment(content)
                    },
                    // -------------------

                    onBackClick = { finish() },
                    onSubmit = { selectedAnswers ->
                        lessonViewModel.submitAnswers(selectedAnswers)
                    }
                )
            }
        }
    }
}