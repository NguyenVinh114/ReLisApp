package com.example.relisapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "results")
data class Result(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val lessonId: Int,
    val score: Int,
    val completedAt: Long = System.currentTimeMillis()
)
