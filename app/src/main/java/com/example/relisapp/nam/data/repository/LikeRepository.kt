package com.example.relisapp.nam.data.repository

import com.example.relisapp.nam.database.dao.LikeDao
import com.example.relisapp.nam.database.entity.Likes

class LikeRepository(private val likeDao: LikeDao) {
    suspend fun getLikes(): List<Likes> = likeDao.getAll()
    suspend fun addLike(likes: Likes) = likeDao.insert(likes)

    suspend fun getAllLikes(): List<Likes> {
        return likeDao.getAll()
    }

    suspend fun deleteLike(like: Likes) {
        likeDao.delete(like)
    }
}
