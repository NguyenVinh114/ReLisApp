package com.example.relisapp.nam.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.relisapp.nam.database.entity.Lessons
import com.example.relisapp.nam.database.entity.User

@Entity(
    primaryKeys = ["userId", "lessonId"],
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
data class FavoriteLessons(
    val userId: Int,
    val lessonId: Int
)
