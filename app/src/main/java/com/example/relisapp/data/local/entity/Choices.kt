package com.example.relisapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Questions::class,
            parentColumns = ["questionId"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Choices(
    @PrimaryKey(autoGenerate = true) val choiceId: Int = 0,
    val questionId: Int,
    val choiceText: String,
    val isCorrect: Int? = 0          // 1 = đúng, 0 = sai
)
