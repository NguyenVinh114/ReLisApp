package com.example.relisapp.data.local.dao

import androidx.room.*
import com.example.relisapp.data.local.entity.Users
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM Users")
    suspend fun getAll(): List<Users>

    @Insert
    suspend fun insert(users: Users)

    // Lưu ý: Đảm bảo tên bảng trong database của bạn đúng là 'users' hoặc tên bạn đã đặt
    @Query("SELECT * FROM users WHERE userId = :id")
    fun getUserById(id: Int): Flow<Users>
}