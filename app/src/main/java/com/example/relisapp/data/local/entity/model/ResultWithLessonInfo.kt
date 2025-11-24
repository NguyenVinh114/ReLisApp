package com.example.relisapp.data.local.entity.model

import com.example.relisapp.data.local.entity.Results

data class ResultWithLessonInfo(
    // Nhúng toàn bộ thông tin kết quả
    val score: Int?,
    val totalQuestions: Int?,
    val createdAt: String,

    // Lấy thêm từ bảng Lessons
    val lessonTitle: String,
    val lessonType: String  // "listening" hoặc "reading"
)