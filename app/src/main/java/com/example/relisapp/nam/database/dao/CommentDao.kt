    package com.example.relisapp.nam.database.dao

    import androidx.room.*
    import com.example.relisapp.nam.database.entity.Comments

    @Dao
    interface CommentDao {
        @Query("SELECT * FROM Comments ORDER BY commentId DESC")
        suspend fun getAll(): List<Comments>

        @Insert
        suspend fun insert(comments: Comments)

        @Delete
        suspend fun delete(comment: Comments)

        @Query("SELECT * FROM Comments WHERE lessonId = :lessonId ORDER BY commentId DESC")
        suspend fun getCommentsByLessonId(lessonId: Int): List<Comments>
    }