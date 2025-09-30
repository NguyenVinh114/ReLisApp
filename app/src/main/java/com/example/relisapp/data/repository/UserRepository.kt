package com.example.relisapp.data.repository

import com.example.relisapp.data.local.UserDao
import com.example.relisapp.model.User

class UserRepository(private val userDao: UserDao) {

    suspend fun insertUser(user: User) = userDao.insertUser(user)

    suspend fun updateUser(user: User) = userDao.updateUser(user)

    suspend fun deleteUser(user: User) = userDao.deleteUser(user)

    suspend fun getUserByUsername(username: String) = userDao.getUserByUsername(username)

    suspend fun getUserByEmail(email: String) = userDao.getUserByEmail(email)

    suspend fun getAllUsers() = userDao.getAllUsers()
}
