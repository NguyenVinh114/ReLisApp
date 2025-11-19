package com.example.relisapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    val userId: Int = 0,

    @ColumnInfo(name = "email")
    val email: String? = null,

    @ColumnInfo(name = "password_hash")
    val passwordHash: String,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "full_name")
    val fullName: String? = null,

    @ColumnInfo(name = "phone_number")
    val phoneNumber: String? = null,

    @ColumnInfo(name = "avatar")
    val avatar: ByteArray? = null,

    @ColumnInfo(name = "account_status")
    val accountStatus: String = "active",

    @ColumnInfo(name = "user_role")
    val userRole: String = "user",

    @ColumnInfo(name = "is_verified")
    val isVerified: Boolean = false
)
