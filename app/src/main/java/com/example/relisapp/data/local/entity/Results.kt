package com.example.relisapp.data.local.entity

import android.R
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Results", // Đặt tên bảng rõ ràng
    foreignKeys = [
        ForeignKey(
            entity = Users::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Lessons::class,
            parentColumns = ["lessonId"],
            childColumns = ["lessonId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Results(
    @PrimaryKey(autoGenerate = true) val resultId: Int = 0,
    val userId: Int,
    val lessonId: Int,
    val score: Int?,
    val totalQuestions: Int?,
    val timeSpent: String?,

    // --- THÊM CỘT NÀY ---
    // Lưu ngày làm bài (VD: "23/11") để hiển thị lên biểu đồ
    // --- CHỈNH SỬA QUAN TRỌNG ---
    val createdAt: String?
)