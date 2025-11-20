package com.example.relisapp.phat.entity

import androidx.room.*

@Entity(
    indices = [
        Index(value = ["username"], unique = true),
        Index(value = ["email"], unique = true)
    ]
)
data class Users(
    // INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,
    // TEXT UNIQUE NOT NULL
    @ColumnInfo(name = "username")
    val username: String,
    // TEXT NOT NULL
    @ColumnInfo(name = "password")
    val password: String,
    // TEXT UNIQUE
    val email: String?,
    // INTEGER
    val age: Int?,
    // TEXT
    val level: String?,
    // TEXT DEFAULT 'user'
    val role: String = "user",
    // TEXT
    val createdAt: String?,
    // INTEGER DEFAULT 0. Dùng Int/Boolean trong Kotlin tùy theo logic app.
    // Dùng Int để khớp chính xác với SQLite INTEGER.
    val isVerified: Int = 0,
    // TEXT
    val fullName: String?,
    // TEXT
    val phoneNumber: String?,
    // BLOB. Trong Room, BLOB thường được ánh xạ thành ByteArray
    val avatar: ByteArray?,
    // TEXT DEFAULT 'active'
    val accountStatus: String = "active"
) /*{
    // Nếu bạn cần so sánh giữa các Users object,
    // bạn cần thêm logic custom cho equals/hashCode để xử lý ByteArray
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Users

        if (userId != other.userId) return false
        if (username != other.username) return false
        if (password != other.password) return false
        if (email != other.email) return false
        if (age != other.age) return false
        if (level != other.level) return false
        if (role != other.role) return false
        if (createdAt != other.createdAt) return false
        if (isVerified != other.isVerified) return false
        if (fullName != other.fullName) return false
        if (phoneNumber != other.phoneNumber) return false
        if (accountStatus != other.accountStatus) return false
        // So sánh nội dung của ByteArray (avatar)
        if (avatar != null) {
            if (other.avatar == null) return false
            if (!avatar.contentEquals(other.avatar)) return false
        } else if (other.avatar != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId
        result = 31 * result + username.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + (age ?: 0)
        result = 31 * result + (level?.hashCode() ?: 0)
        result = 31 * result + role.hashCode()
        result = 31 * result + (createdAt?.hashCode() ?: 0)
        result = 31 * result + isVerified
        result = 31 * result + (fullName?.hashCode() ?: 0)
        result = 31 * result + (phoneNumber?.hashCode() ?: 0)
        result = 31 * result + (avatar?.contentHashCode() ?: 0)
        result = 31 * result + accountStatus.hashCode()
        return result
    }
}*/