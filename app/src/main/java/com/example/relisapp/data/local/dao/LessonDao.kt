package com.example.relisapp.data.local.dao

import androidx.room.*
import com.example.relisapp.data.local.entity.Comments
import com.example.relisapp.data.local.entity.Lessons
import com.example.relisapp.data.local.entity.model.CommentWithUser
import com.example.relisapp.data.local.entity.model.QuestionWithChoices
import kotlinx.coroutines.flow.Flow // Cần import này

@Dao
interface LessonDao {
    // Sửa: Dùng Flow để Room tự động gửi cập nhật tới ViewModel
    @Query("SELECT * FROM Lessons")
    fun getAllLessonsFlow(): Flow<List<Lessons>>

    @Query("SELECT * FROM Lessons")
    suspend fun getAll(): List<Lessons>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lessons: Lessons)

    // Thêm các hàm lấy bài học theo ID nếu cần sau này...
    @Transaction
    @Query("SELECT * FROM Questions WHERE lessonId = :lessonId")
    suspend fun getQuestionsWithChoicesForLesson(lessonId: Int): List<QuestionWithChoices>


    @Query("SELECT * FROM Lessons WHERE categoryId = :categoryId ORDER BY lessonId ASC")
    suspend fun getLessonsByCategoryId(categoryId: Int): List<Lessons>

    @Query("SELECT * FROM Lessons WHERE lessonId = :lessonId LIMIT 1")
    suspend fun getLessonById(lessonId: Int): Lessons?


    // 1. Lấy comment kèm thông tin người dùng (Join bảng)
    // Sắp xếp bài mới nhất lên đầu (ORDER BY commentId DESC)
    @Query("""
        SELECT Comments.*, Users.username, Users.fullName 
        FROM Comments 
        LEFT JOIN Users ON Comments.userId = Users.userId 
        WHERE lessonId = :lessonId 
        ORDER BY commentId DESC
    """)
    fun getCommentsForLesson(lessonId: Int): Flow<List<CommentWithUser>>

    // 2. Thêm comment
    @Insert
    suspend fun insertComment(comment: Comments)
}