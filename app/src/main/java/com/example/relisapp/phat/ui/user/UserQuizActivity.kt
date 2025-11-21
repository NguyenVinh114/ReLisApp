// File: phat/ui/user/UserQuizActivity.kt
package com.example.relisapp.phat.ui.user

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.relisapp.phat.data.AppDatabase
import com.example.relisapp.phat.repository.LessonRepository
import com.example.relisapp.phat.ui.theme.UserFreshTheme
import com.example.relisapp.phat.ui.user.screen.UserQuizScreen // Màn hình này sẽ được tạo ở bước 2
import com.example.relisapp.phat.viewmodel.LessonViewModel
import com.example.relisapp.phat.viewmodel.LessonViewModelFactory

class UserQuizActivity : ComponentActivity() {

    private lateinit var lessonViewModel: LessonViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Lấy lessonId được truyền từ màn hình trước
        val lessonId = intent.getIntExtra("LESSON_ID", -1)
        val lessonTitle = intent.getStringExtra("LESSON_TITLE")
        if (lessonId == -1) {
            Toast.makeText(this, "Error: Lesson not found.", Toast.LENGTH_SHORT).show()
            finish() // Đóng Activity nếu không có lessonId hợp lệ
            return
        }

        // 2. Khởi tạo ViewModel (giống như các Activity khác)
        val db = AppDatabase.getDatabase(this)
        val repo = LessonRepository(db.lessonDao()) // Đảm bảo LessonRepository có thể truy cập QuestionDao nếu bạn đã tái cấu trúc
        lessonViewModel = ViewModelProvider(this, LessonViewModelFactory(repo))[LessonViewModel::class.java]

        // 3. Yêu cầu ViewModel tải dữ liệu cho bài quiz
        // Bạn đã có sẵn hàm loadLesson() trong ViewModel rồi
        lessonViewModel.loadLesson(lessonId)


        setContent {
            UserFreshTheme {
                val questions by lessonViewModel.questions.collectAsStateWithLifecycle()
                val score by lessonViewModel.score.collectAsStateWithLifecycle()
                val isLoading by lessonViewModel.isLoadingQuiz.collectAsStateWithLifecycle() // Lấy state mới
                val audioPath by lessonViewModel.audioPath.collectAsStateWithLifecycle()
                val quizResults by lessonViewModel.quizResults.collectAsStateWithLifecycle()
                // Truyền state mới vào UserQuizScreen
                UserQuizScreen(
/*
                    lessonId = lessonId,
*/
                    lessonTitle = lessonTitle,
                    questionsWithChoices = questions,
                    isLoading = isLoading,
                    audioPath = audioPath,// Truyền vào đây
                    score = score,
                    quizResults = quizResults,
                    onBackClick = { finish() },
                    onSubmit = { selectedAnswers ->
                        lessonViewModel.submitAnswers(selectedAnswers)
                    }
                )
            }
        }
    }
}
