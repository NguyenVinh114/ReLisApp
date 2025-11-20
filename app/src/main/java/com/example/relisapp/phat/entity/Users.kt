package com.example.relisapp.phat.entity

import androidx.room.*

@Entity(
    indices = [
        Index(value = ["username"], unique = true),
        Index(value = ["email"], unique = true)
    ]
)
data class Users(
    @PrimaryKey(autoGenerate = true) val userId: Int = 0,
    val username: String,
    val password: String,
    val email: String?,
    val age: Int?,
    val level: String?,
    val role: String = "user",   // default khớp SQLite
    val createdAt: String?
)

