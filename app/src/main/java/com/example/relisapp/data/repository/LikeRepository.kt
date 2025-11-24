package com.example.relisapp.data.repository

import com.example.relisapp.data.local.dao.LikeDao
import com.example.relisapp.data.local.entity.Likes

class LikeRepository(private val likeDao: LikeDao) {
    suspend fun getLikes(): List<Likes> = likeDao.getAll()
    suspend fun addLike(likes: Likes) = likeDao.insert(likes)
}
