package com.example.relisapp.data.local

import androidx.room.*
import com.example.relisapp.model.Result

@Dao
interface ResultDao {
    @Insert
    suspend fun insertResult(result: Result)

    @Update
    suspend fun updateResult(result: Result)

    @Delete
    suspend fun deleteResult(result: Result)

    @Query("SELECT * FROM results WHERE userId = :userId")
    suspend fun getResultsByUser(userId: Int): List<Result>

    @Query("SELECT * FROM results WHERE lessonId = :lessonId")
    suspend fun getResultsByLesson(lessonId: Int): List<Result>
}
