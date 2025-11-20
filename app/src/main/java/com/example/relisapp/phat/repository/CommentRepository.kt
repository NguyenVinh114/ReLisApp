package com.example.relisapp.phat.repository

import com.example.relisapp.phat.dao.CommentDao
import com.example.relisapp.phat.entity.Comments

class CommentRepository(private val commentDao: CommentDao) {
    suspend fun getComments(): List<Comments> = commentDao.getAll()
    suspend fun addComment(comments: Comments) = commentDao.insert(comments)
}
