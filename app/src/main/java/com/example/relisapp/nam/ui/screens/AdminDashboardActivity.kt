package com.example.relisapp.nam.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.relisapp.nam.data.repository.CommentRepository
import com.example.relisapp.nam.data.repository.LessonRepository
import com.example.relisapp.nam.data.repository.LikeRepository
import com.example.relisapp.nam.data.repository.UserRepository
import com.example.relisapp.nam.database.AppDatabase
import com.example.relisapp.nam.ui.theme.LearnTheme
import com.example.relisapp.nam.viewmodel.CommentModerationViewModel
import com.example.relisapp.nam.viewmodel.CommentModerationViewModelFactory
import com.example.relisapp.nam.viewmodel.LikeStatsViewModel
import com.example.relisapp.nam.viewmodel.LikeStatsViewModelFactory

class AdminDashboardActivity : ComponentActivity() {

    private lateinit var commentVM: CommentModerationViewModel
    private lateinit var likeStatsVM: LikeStatsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Khởi tạo DB chung
        val db = AppDatabase.getDatabase(this)
        val userRepo = UserRepository(db.userDao())
        val lessonRepo = LessonRepository(db.lessonDao())
        val commentRepo = CommentRepository(db.commentDao())
        val likeRepo = LikeRepository(db.likeDao())

        // 2. Tạo ViewModel cho Tab 1 (Comment)
        val commentFactory = CommentModerationViewModelFactory(commentRepo, userRepo, lessonRepo)
        commentVM = ViewModelProvider(this, commentFactory)[CommentModerationViewModel::class.java]

        // 3. Tạo ViewModel cho Tab 2 (Like Stats)
        val likeFactory = LikeStatsViewModelFactory(likeRepo, userRepo, lessonRepo)
        likeStatsVM = ViewModelProvider(this, likeFactory)[LikeStatsViewModel::class.java]

        // 4. Hiển thị UI Dashboard
        setContent {
            LearnTheme {
                AdminDashboardScreen(commentVM, likeStatsVM, onBack = { finish() })
            }
        }
    }
}