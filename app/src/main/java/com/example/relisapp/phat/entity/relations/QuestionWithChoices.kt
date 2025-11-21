// File: phat/entity/relations/QuestionWithChoices.kt

package com.example.relisapp.phat.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.relisapp.phat.entity.Choices
import com.example.relisapp.phat.entity.Questions

data class QuestionWithChoices(
    @Embedded
    val question: Questions,

    @Relation(
        parentColumn = "questionId", // Cột trong bảng Questions
        entityColumn = "questionId"  // Cột tương ứng trong bảng Choices
    )
    val choices: List<Choices>
)
