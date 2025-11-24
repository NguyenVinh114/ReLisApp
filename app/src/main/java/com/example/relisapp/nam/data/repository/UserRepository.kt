package com.example.relisapp.nam.data.repository

import com.example.relisapp.nam.database.dao.UserDao
import com.example.relisapp.nam.database.entity.User
import kotlinx.coroutines.flow.Flow
import androidx.room.Transaction

class UserRepository(private val userDao: UserDao) {

    // Register
    suspend fun register(user: User): Long {
        return try {
            userDao.registerUser(user)
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    // Login
    suspend fun login(input: String, password: String): User? {
        return userDao.login(input, password)
    }

    // Get by email
    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    // Get by username
    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }

    // Get by phone
    suspend fun getUserByPhone(phone: String): User? {
        return userDao.getUserByPhone(phone)
    }

    // Get by ID
    suspend fun getUserById(userId: Int): User? {
        return userDao.getUserById(userId)
    }

    // Update status
    suspend fun updateUserStatus(userId: Int, status: String) {
        userDao.updateStatus(userId, status)
    }

    // Logout
    suspend fun logout(userId: Int) {
        userDao.logout(userId)
    }

    // Update password
    suspend fun updatePassword(userId: Int, newPassword: String) {
        userDao.updatePassword(userId, newPassword)
    }


    // Update avatar
    suspend fun updateAvatar(userId: Int, avatar: ByteArray?) {
        userDao.updateAvatar(userId, avatar)
    }

    // Check phone exists
    suspend fun isPhoneExists(phone: String): Boolean {
        return userDao.getUserByPhone(phone) != null
    }

    // Admin: get all users
    fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers()
    }

    // Delete user
    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }


    // Delete user by ID
    suspend fun deleteUserById(userId: Int) {
        userDao.deleteUserById(userId)
    }

    // Update user object
    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun updateUsername(userId: Int, newName: String) {
        userDao.updateUsername(userId, newName)
    }

    suspend fun updateFullName(userId: Int, newName: String) {
        userDao.updateFullName(userId, newName)
    }

    @Transaction
    suspend fun updateUserProfile(
        userId: Int,
        username: String,
        fullName: String,
        avatar: ByteArray?,
        age: Int?
    ) {
        userDao.updateUsername(userId, username)
        userDao.updateFullName(userId, fullName)
        userDao.updateAvatar(userId, avatar)
        userDao.updateAge(userId, age)   // ⭐ Thêm dòng này
    }
}
