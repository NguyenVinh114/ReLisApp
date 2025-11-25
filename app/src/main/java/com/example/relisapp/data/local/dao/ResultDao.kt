package com.example.relisapp.data.local.dao

import androidx.room.*
import com.example.relisapp.data.local.entity.Results
import com.example.relisapp.data.local.entity.model.ResultWithLessonInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultDao {
    @Query("SELECT * FROM Results")
    suspend fun getAll(): List<Results>

    @Insert
    suspend fun insertResult(result: Results)


    @Query("""
        SELECT r.score, r.totalQuestions, r.createdAt, l.title as lessonTitle, l.type as lessonType
        FROM Results r
        INNER JOIN Lessons l ON r.lessonId = l.lessonId
        WHERE r.userId = :userId
        ORDER BY r.resultId DESC
    """)
    fun getAllHistory(userId: Int): Flow<List<ResultWithLessonInfo>>

    // 3. Lấy dữ liệu cho Biểu Đồ (Lọc theo kỹ năng: Listening/Reading)
    // Lấy 7 lần làm bài gần nhất của kỹ năng đó
    @Query("""
        SELECT r.score, r.totalQuestions, r.createdAt, l.title as lessonTitle, l.type as lessonType
        FROM Results r
        INNER JOIN Lessons l ON r.lessonId = l.lessonId
        WHERE r.userId = :userId AND l.type = :skillType
        ORDER BY r.resultId ASC
        LIMIT 7
    """)
    fun getChartData(userId: Int, skillType: String): Flow<List<ResultWithLessonInfo>>
}