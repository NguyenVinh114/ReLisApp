package com.example.relisapp.nam.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(entity = Lessons::class, parentColumns = ["lessonId"], childColumns = ["lessonId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class Comments(
    @PrimaryKey(autoGenerate = true) val commentId: Int = 0,
    val userId: Int,
    val lessonId: Int,
    val content: String,
    val createdAt: String?
)
