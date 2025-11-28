package com.example.relisapp.phat.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Users::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(entity = Lessons::class, parentColumns = ["lessonId"], childColumns = ["lessonId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class Results(
    @PrimaryKey(autoGenerate = true) val resultId: Int = 0,
    val userId: Int,
    val lessonId: Int,
    val score: Int?,
    val totalQuestions: Int?,
    val timeSpent: String?,
    val createdAt: String? = null
)