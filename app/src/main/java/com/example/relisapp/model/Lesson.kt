package com.example.relisapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lessons")
data class Lesson(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String? = null,
    val audioUrl: String? = null,
    val content: String? = null,
    val type: String, // "listening" or "reading"
    val level: String? = null // ví dụ: beginner, intermediate, advanced

)
