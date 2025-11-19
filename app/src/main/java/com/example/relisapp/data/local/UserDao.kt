package com.example.relisapp.data.local
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import com.example.relisapp.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // Đăng ký
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun registerUser(user: User): Long

    // Đăng nhập
    @Query("""
        SELECT * FROM users 
        WHERE (username = :input OR phone_number = :input)
        AND password_hash = :password
        LIMIT 1
    """)
    suspend fun login(input: String, password: String): User?

    // users
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE phone_number = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): User?

    @Query("SELECT * FROM users WHERE user_id = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): User?

    // Đăng xuất
    @Query("UPDATE users SET account_status = 'inactive' WHERE user_id = :userId")
    suspend fun logout(userId: Int)

    @Query("UPDATE users SET account_status = :status WHERE user_id = :userId")
    suspend fun updateStatus(userId: Int, status: String)


    // Get all users (Admin)
    @Query("SELECT * FROM users ORDER BY user_id ASC")
    fun getAllUsers(): Flow<List<User>>

    // Delete account
    @Delete
    suspend fun deleteUser(user: User)

    @Query("DELETE FROM users WHERE user_id = :userId")
    suspend fun deleteUserById(userId: Int)


    //  Update user
    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET password_hash = :newPassword WHERE user_id = :userId")
    suspend fun updatePassword(userId: Int, newPassword: String)

    @Query("UPDATE users SET avatar = :avatar WHERE user_id = :userId")
    suspend fun updateAvatar(userId: Int, avatar: ByteArray?)


}
