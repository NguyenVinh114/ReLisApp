package com.example.relisapp.phat.repository

import com.example.relisapp.phat.dao.UserDao
import com.example.relisapp.phat.entity.Users

class UserRepository(private val userDao: UserDao) {
    suspend fun getUsers(): List<Users> = userDao.getAll()
    suspend fun addUser(users: Users) = userDao.insert(users)
}
