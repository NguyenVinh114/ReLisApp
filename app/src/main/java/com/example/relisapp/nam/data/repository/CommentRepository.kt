package com.example.relisapp.nam.data.repository

import com.example.relisapp.nam.database.dao.CommentDao
import com.example.relisapp.nam.database.entity.Comments

class CommentRepository(private val commentDao: CommentDao) {

    // Lấy tất cả comment (dùng cho Moderation)
    suspend fun getAllComments(): List<Comments> {
        return commentDao.getAll()
    }

    // Thêm comment
    suspend fun addComment(comment: Comments) {
        commentDao.insert(comment)
    }

    // Xóa comment
    suspend fun deleteComment(comment: Comments) {
        commentDao.delete(comment)
    }

    suspend fun getCommentsByLessonId(lessonId: Int): List<Comments> {
        return commentDao.getCommentsByLessonId(lessonId)
    }
}