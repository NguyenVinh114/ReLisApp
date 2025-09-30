package com.example.relisapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feedbacks")
data class Feedback(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val message: String,
    val createdAt: Long = System.currentTimeMillis()
)
