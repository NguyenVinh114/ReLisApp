package com.example.relisapp.nam.database.dao

import androidx.room.*
import com.example.relisapp.nam.database.entity.Lessons

@Dao
interface LessonDao {
    @Query("SELECT * FROM Lessons ORDER BY lessonId DESC")
    suspend fun getAll(): List<Lessons>

    @Query("SELECT * FROM Lessons WHERE lessonId = :id")
    suspend fun getLessonById(id: Int): Lessons?

    @Query("SELECT * FROM Lessons WHERE categoryId = :categoryId")
    suspend fun getByCategory(categoryId: Int): List<Lessons>

    @Query("SELECT * FROM Lessons WHERE type = :type")
    suspend fun getByType(type: String): List<Lessons>

    @Query("SELECT * FROM Lessons WHERE level = :level")
    suspend fun getByLevel(level: String): List<Lessons>

    // Đã sửa: Tìm theo title hoặc content (bỏ description)
    @Query("SELECT * FROM Lessons WHERE title LIKE :query OR content LIKE :query")
    suspend fun search(query: String): List<Lessons>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lesson: Lessons): Long

    @Update
    suspend fun update(lesson: Lessons)

    @Delete
    suspend fun delete(lesson: Lessons)
}