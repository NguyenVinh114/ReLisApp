// In file: phat/ui/admin/AddEditQuestionActivity.kt

package com.example.relisapp.phat.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.relisapp.nam.ui.screens.user.ProfileActivity
import com.example.relisapp.phat.data.AppDatabase
import com.example.relisapp.phat.repository.QuestionRepository
import com.example.relisapp.phat.ui.admin.screen.AddEditQuestionScreen
import com.example.relisapp.phat.ui.admin.screen.BaseAdminScreen
import com.example.relisapp.phat.ui.theme.AdminProTheme
import com.example.relisapp.phat.viewmodel.QuestionViewModel
import com.example.relisapp.phat.viewmodel.QuestionViewModelFactory

class AddEditQuestionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val lessonId = intent.getIntExtra("LESSON_ID", -1)
        // Lấy questionId, mặc định là -1 nếu không có (chế độ thêm mới)
        val questionId = intent.getIntExtra("QUESTION_ID", -1)
        val isEditMode = questionId != -1

        val database = AppDatabase.getDatabase(applicationContext)
        val questionRepository = QuestionRepository(database.questionDao())
        val viewModelFactory = QuestionViewModelFactory(questionRepository)

        setContent {
            AdminProTheme {
                val questionViewModel: QuestionViewModel = viewModel(factory = viewModelFactory)

                // [QUAN TRỌG] Tải dữ liệu nếu là chế độ sửa
                LaunchedEffect(key1 = isEditMode) {
                    if (isEditMode) {
                        questionViewModel.loadQuestionDetails(questionId)
                    } else {
                        // Đảm bảo state sạch khi vào màn hình thêm mới
                        questionViewModel.clearQuestionDetails()
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BaseAdminScreen(
                        // Thay đổi tiêu đề tùy theo chế độ
                        title = if (isEditMode) "Edit Question" else "Add New Question",
                        onDashboard = { /* TODO */ },
                        onManageCategories = { /* TODO */ },
                        onManageLessons = { /* TODO */ },
                        onManageUsers = { /* TODO */ },
                        onFeedback = { /* TODO */ },
                        onLogout = { /* TODO */ },
                        onIconUserClick = {
                            startActivity(Intent(this, ProfileActivity::class.java))
                        }
                    ) { modifier ->
                        AddEditQuestionScreen(
                            modifier = modifier,
                            lessonId = lessonId,
                            // --- [SỬA 1] TRUYỀN questionId VÀO SCREEN ---
                            questionId = if (isEditMode) questionId else null,
                            questionViewModel = questionViewModel,
                            onSave = {
                                finish()
                            }
                        )
                    }
                }
            }
        }
    }
}
