package com.example.relisapp.nam.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Lessons",
    foreignKeys = [
        ForeignKey(
            entity = Categories::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["createdBy"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class Lessons(
    @PrimaryKey(autoGenerate = true) val lessonId: Int = 0,
    val categoryId: Int,
    val title: String,
    val type: String,
    val content: String? = null,
    val audioPath: String? = null,
    val transcript: String? = null,
    val level: String? = null,
    val createdBy: Int? = null,
    val createdAt: String? = null
)
