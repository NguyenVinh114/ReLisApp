package com.example.relisapp.nam.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "study_sessions",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id", "date"], unique = true),
        Index(value = ["user_id"])
    ]
)
data class StudySession(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "lessons_completed", defaultValue = "0")
    val lessonsCompleted: Int = 0,

    @ColumnInfo(name = "listening_count", defaultValue = "0")
    val listeningCount: Int = 0,

    @ColumnInfo(name = "reading_count", defaultValue = "0")
    val readingCount: Int = 0,

    @ColumnInfo(name = "total_time_minutes", defaultValue = "0")
    val totalTimeMinutes: Int = 0,

    @ColumnInfo(name = "score_average", defaultValue = "0.0")
    val scoreAverage: Float = 0f,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis()
)