package com.example.relisapp.data.local.entity

import androidx.room.*

@Entity(
    tableName = "Users",
    indices = [
        Index(value = ["username"], unique = true),
        Index(value = ["email"], unique = true)
    ]
)
data class Users(
    @PrimaryKey(autoGenerate = true) val userId: Int = 0,
    val username: String,
    val password: String,
    val email: String? = null,
    val age: Int? = null,
    val level: String? = null,
    val role: String? = "user",
    val createdAt: String? = null,
    val isVerified: Int? = 0,
    val fullName: String? = null,
    val phoneNumber: String? = null,
    val avatar: ByteArray? = null,
    val accountStatus: String? = "active"
)
