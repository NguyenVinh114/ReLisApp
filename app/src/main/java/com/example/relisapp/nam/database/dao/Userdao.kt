package com.example.relisapp.nam.database.dao

import androidx.room.*
import com.example.relisapp.nam.database.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // ⭐ [NEW] Cập nhật Streak cho User
    @Query("UPDATE Users SET currentStreak = :current, longestStreak = :longest, lastStudyDate = :lastDate WHERE userId = :userId")
    suspend fun updateStreak(userId: Int, current: Int, longest: Int, lastDate: String)

    // Lấy thông tin User (đã bao gồm streak)
    @Query("SELECT * FROM Users WHERE userId = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): User?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun registerUser(user: User): Long

    @Query("""
        SELECT * FROM Users
        WHERE (username = :input OR phoneNumber = :input)
        AND password = :password
        LIMIT 1
    """)
    suspend fun login(input: String, password: String): User?

    @Query("SELECT * FROM Users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM Users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM Users WHERE phoneNumber = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): User?

    @Query("UPDATE Users SET accountStatus = 'inactive' WHERE userId = :userId")
    suspend fun logout(userId: Int)

    @Query("UPDATE Users SET accountStatus = :status WHERE userId = :userId")
    suspend fun updateStatus(userId: Int, status: String)

    @Query("SELECT * FROM Users ORDER BY userId ASC")
    fun getAllUsers(): Flow<List<User>>

    @Delete
    suspend fun deleteUser(user: User)

    @Query("DELETE FROM Users WHERE userId = :userId")
    suspend fun deleteUserById(userId: Int)

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE Users SET avatar = :avatar WHERE userId = :userId")
    suspend fun updateAvatar(userId: Int, avatar: ByteArray?)

    @Query("UPDATE Users SET password = :newPassword WHERE userId = :userId")
    suspend fun updatePassword(userId: Int, newPassword: String)

    @Query("UPDATE Users SET username = :newName WHERE userId = :userId")
    suspend fun updateUsername(userId: Int, newName: String)

    @Query("UPDATE Users SET fullName = :newName WHERE userId = :userId")
    suspend fun updateFullName(userId: Int, newName: String)

    @Query("UPDATE Users SET age = :age WHERE userId = :userId")
    suspend fun updateAge(userId: Int, age: Int?)

    @Query("SELECT * FROM Users ORDER BY currentStreak DESC, lastStudyDate DESC LIMIT 13")
    suspend fun getLeaderboard(): List<User>
}