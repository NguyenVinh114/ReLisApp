package com.example.relisapp.nam.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

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

    @ColumnInfo(defaultValue = "'user'")
    val role: String? = "user",

    val createdAt: String? = null,

    val fullName: String? = null,
    val phoneNumber: String? = null,
    val avatar: ByteArray? = null,

    @ColumnInfo(defaultValue = "'active'")
    val accountStatus: String? = "active",

    @ColumnInfo(name = "isVerified", defaultValue = "0")
    val isVerified: Int? = 0,

    @ColumnInfo(defaultValue = "0")
    val currentStreak: Int = 0,

    @ColumnInfo(defaultValue = "0")
    val longestStreak: Int = 0,

    @ColumnInfo(defaultValue = "NULL")
    val lastStudyDate: String? = null
)
