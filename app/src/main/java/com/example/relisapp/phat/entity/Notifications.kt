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
        )
    ]
)
data class Notifications(
    @PrimaryKey(autoGenerate = true) val notificationId: Int = 0,
    val userId: Int,
    val title: String,
    val content: String?,
    val createdAt: String?,
    val isRead: Int? = 0 // 0 = chưa đọc, 1 = đã đọc
)