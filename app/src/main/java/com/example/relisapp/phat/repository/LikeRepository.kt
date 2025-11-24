package com.example.relisapp.phat.repository

import com.example.relisapp.phat.dao.LikeDao
import com.example.relisapp.phat.entity.Likes

class LikeRepository(private val likeDao: LikeDao) {
    suspend fun getLikes(): List<Likes> = likeDao.getAll()
    suspend fun addLike(likes: Likes) = likeDao.insert(likes)
}
