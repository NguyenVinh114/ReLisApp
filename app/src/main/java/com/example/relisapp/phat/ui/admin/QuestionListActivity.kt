// In file: phat/ui/admin/QuestionListActivity.kt
package com.example.relisapp.phat.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.example.relisapp.nam.ui.screens.ProfileActivity
import com.example.relisapp.phat.data.AppDatabase
import com.example.relisapp.phat.entity.Questions
import com.example.relisapp.phat.repository.LessonRepository
import com.example.relisapp.phat.repository.QuestionRepository
import com.example.relisapp.phat.ui.admin.screen.BaseAdminScreen
import com.example.relisapp.phat.ui.admin.screen.QuestionListScreen
import com.example.relisapp.phat.ui.theme.AdminProTheme
import com.example.relisapp.phat.viewmodel.LessonViewModel // <-- IMPORT
import com.example.relisapp.phat.viewmodel.LessonViewModelFactory // <-- IMPORT
import com.example.relisapp.phat.viewmodel.QuestionViewModel
import com.example.relisapp.phat.viewmodel.QuestionViewModelFactory

class QuestionListActivity : ComponentActivity() {

    // Khởi tạo QuestionViewModel
    private val questionViewModel: QuestionViewModel by lazy {
        val factory = QuestionViewModelFactory(QuestionRepository(AppDatabase.getDatabase(this).questionDao()))
        ViewModelProvider(this, factory)[QuestionViewModel::class.java]
    }

    // [THÊM MỚI] Khởi tạo LessonViewModel
    private val lessonViewModel: LessonViewModel by lazy {
        val factory = LessonViewModelFactory(LessonRepository(AppDatabase.getDatabase(this).lessonDao()))
        ViewModelProvider(this, factory)[LessonViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val lessonId = intent.getIntExtra("LESSON_ID", -1)
        if (lessonId == -1) {
            Toast.makeText(this, "Invalid Lesson ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // [SỬA] Gọi riêng từng ViewModel để tải dữ liệu của nó
        questionViewModel.loadQuestionsForLesson(lessonId)
        lessonViewModel.loadLessonDetails(lessonId)

        setContent {
            AdminProTheme {
                // [SỬA] Thu thập state từ cả hai ViewModel
                val questions by questionViewModel.questionsWithChoices.collectAsState()
                val lesson by lessonViewModel.lessonDetails.collectAsState()

                BaseAdminScreen(
                    title = lesson?.title ?: "Questions",
                    onManageUsers ={},
                    onDashboard = {},
                    onFeedback = {},
                    onManageCategories = {},
                    onLogout = {},
                    onManageLessons = { finish() },
                    onIconUserClick = {
                        startActivity(Intent(this, ProfileActivity::class.java))
                    }
                )  { modifier ->
                    QuestionListScreen(
                        modifier = modifier,
                        lessonId = lessonId,
                        lessonContent = lesson?.content,
                        questionsWithChoices = questions,

                        // --- [SỬA 1] TRIỂN KHAI LOGIC ĐIỀU HƯỚNG ---
                        onAddNewQuestion = {
                            // Tạo Intent để mở màn hình Add/Edit
                            val intent = Intent(this, AddEditQuestionActivity::class.java)
                            // Chỉ cần truyền LESSON_ID vì đây là thêm mới
                            intent.putExtra("LESSON_ID", lessonId)
                            startActivity(intent)
                        },
                        onDeleteQuestion = { questionToDelete ->
                            // TODO: Hiển thị dialog xác nhận trước khi xóa
                            questionViewModel.deleteQuestion(questionToDelete.question)
                            Toast.makeText(this, "Question deleted", Toast.LENGTH_SHORT).show()
                        },
                        onEditQuestion = { questionToEdit ->
                            // Tạo Intent để mở màn hình Add/Edit
                            val intent = Intent(this, AddEditQuestionActivity::class.java)
                            // Truyền cả LESSON_ID và QUESTION_ID
                            intent.putExtra("LESSON_ID", lessonId)
                            intent.putExtra("QUESTION_ID", questionToEdit.question.questionId)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val lessonId = intent.getIntExtra("LESSON_ID", -1)
        if (lessonId != -1) {
            // Tải lại dữ liệu từ cả hai ViewModel
            questionViewModel.loadQuestionsForLesson(lessonId)
            lessonViewModel.loadLessonDetails(lessonId)
        }
    }
}
