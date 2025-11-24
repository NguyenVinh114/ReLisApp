package com.example.relisapp.data.local.entity.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.relisapp.data.local.entity.Choices
import com.example.relisapp.data.local.entity.Questions

data class QuestionWithChoices(
    @Embedded
    val question: Questions,

    @Relation(
        parentColumn = "questionId", // Cột trong bảng Questions
        entityColumn = "questionId"  // Cột tương ứng trong bảng Choices
    )
    val choices: List<Choices>
)
