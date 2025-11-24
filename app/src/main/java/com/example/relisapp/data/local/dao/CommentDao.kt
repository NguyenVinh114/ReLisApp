package com.example.relisapp.data.local.dao

import androidx.room.*
import com.example.relisapp.data.local.entity.Comments
import com.example.relisapp.data.local.entity.model.CommentWithUser

@Dao
interface CommentDao {
    // Lấy comment của một bài học cụ thể, kèm tên người dùng
    // Sắp xếp giảm dần theo ID (comment mới nhất lên đầu)
    @Query("""
        SELECT C.*, U.username, U.fullName 
        FROM Comments C 
        INNER JOIN Users U ON C.userId = U.userId 
        WHERE C.lessonId = :lessonId 
        ORDER BY C.commentId DESC
    """)
    suspend fun getCommentsByLesson(lessonId: Int): List<CommentWithUser>

    @Insert
    suspend fun insert(comments: Comments)
}