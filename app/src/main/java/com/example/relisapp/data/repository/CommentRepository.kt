package com.example.relisapp.data.repository

import com.example.relisapp.data.local.dao.CommentDao
import com.example.relisapp.data.local.entity.Comments
import com.example.relisapp.data.local.entity.model.CommentWithUser

class CommentRepository(private val commentDao: CommentDao) {
    // Đổi hàm này để nhận lessonId và trả về CommentWithUser
    suspend fun getCommentsByLesson(lessonId: Int): List<CommentWithUser> =
        commentDao.getCommentsByLesson(lessonId)

    suspend fun addComment(comments: Comments) = commentDao.insert(comments)
}