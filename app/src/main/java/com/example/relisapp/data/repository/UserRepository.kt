package com.example.relisapp.data.repository

import com.example.relisapp.data.local.UserDao
import com.example.relisapp.model.User

class UserRepository(private val userDao: UserDao) {

    suspend fun register(user: User): Long {
        return try {
            userDao.registerUser(user)
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    suspend fun login(input: String, password: String): User? {
        return userDao.login(input, password)
    }

    // Get user by email
    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    // Xóa người dùng
    suspend fun deleteUser(user: User) = userDao.deleteUser(user)

    // Get user by username
    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }

    suspend fun getUserById(id: Int): User? {
        return userDao.getUserById(id)
    }


    //Get user by phone number
    suspend fun getUserByPhone(phone: String): User? {
        return userDao.getUserByPhone(phone)
    }

    // Update user status
    suspend fun updateUserStatus(userId: Int, status: String) {
        userDao.updateStatus(userId, status)
    }

    // Logout user
    suspend fun logout(userId: Int) {
        userDao.logout(userId)
    }

    // Update password
    suspend fun updatePassword(userId: Int, newPassword: String) {
        userDao.updatePassword(userId, newPassword)
    }

    suspend fun isPhoneExists(phone: String): Boolean {
        return userDao.getUserByPhone(phone) != null
    }

    suspend fun updateAvatar(userId: Int, avatar: ByteArray?) {
        userDao.updateAvatar(userId, avatar)
    }

    suspend fun updateUsername(id: Int, newName: String) {
        userDao.updateUsername(id, newName)
    }

    suspend fun updateFullName(id: Int, fullName: String) {
        userDao.updateFullName(id, fullName)
    }

}