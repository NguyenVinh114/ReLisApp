package com.example.relisapp.nam.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.relisapp.nam.database.entity.Questions


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
