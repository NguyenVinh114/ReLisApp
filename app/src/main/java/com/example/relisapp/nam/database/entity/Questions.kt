package com.example.relisapp.nam.database.entity
import androidx.room.ForeignKey
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(entity = Lessons::class, parentColumns = ["lessonId"], childColumns = ["lessonId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class Questions(
    @PrimaryKey(autoGenerate = true) val questionId: Int = 0,
    val lessonId: Int,
    val questionText: String,
    val questionType: String?,      // trắc nghiệm / điền từ
    val correctAnswer: String?
)
