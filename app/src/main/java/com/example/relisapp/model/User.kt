package com.example.relisapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val email: String,
    val password: String,
    val fullName: String? = null,
    val avatarUrl: String? = null,
    val role: String = "student" // student / admin
)
