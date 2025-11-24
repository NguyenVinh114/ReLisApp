package com.example.relisapp.nam.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "Users",
    indices = [
        Index(value = ["username"], unique = true),
        Index(value = ["email"], unique = true)
    ]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,
    val username: String,
    val password: String,
    val email: String? = null,
    val age: Int? = null,
    val level: String? = null,
    val role: String? = "user",
    val createdAt: String? = null,
    val fullName: String? = null,
    val phoneNumber: String? = null,
    val avatar: ByteArray? = null,
    val accountStatus: String? = "active",
    val isVerified: Boolean? = false
)