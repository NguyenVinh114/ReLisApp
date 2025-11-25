package com.example.relisapp.data.repository

import com.example.relisapp.data.local.dao.UserDao
import com.example.relisapp.data.local.entity.Users

class UserRepository(private val userDao: UserDao) {
    suspend fun getUsers(): List<Users> = userDao.getAll()
    suspend fun addUser(users: Users) = userDao.insert(users)
}
