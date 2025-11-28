package com.example.relisapp.nam.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.relisapp.nam.database.entity.StudySession
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySessionDao {

    // ⭐ [UPDATE] Insert trả về ID
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySession): Long

    // ⭐ [UPDATE] Update session
    @Update
    suspend fun updateSession(session: StudySession)

    // ⭐ [UPDATE] Get session by date AND userId
    @Query("SELECT * FROM study_sessions WHERE date = :date AND user_id = :userId LIMIT 1")
    suspend fun getSessionByDate(userId: Int, date: String): StudySession?

    // ⭐ [UPDATE] Get recent sessions for specific user
    @Query("SELECT * FROM study_sessions WHERE user_id = :userId ORDER BY date DESC LIMIT :limit")
    suspend fun getRecentSessions(userId: Int, limit: Int): List<StudySession>

    // ⭐ [UPDATE] Flow cho specific user
    @Query("SELECT * FROM study_sessions WHERE user_id = :userId ORDER BY date DESC")
    fun getAllSessionsFlow(userId: Int): Flow<List<StudySession>>

    // ⭐ [UPDATE] Count total days for specific user
    @Query("SELECT COUNT(*) FROM study_sessions WHERE user_id = :userId")
    suspend fun getTotalDays(userId: Int): Int

    // ⭐ [UPDATE] Get sessions by month for specific user
    @Query("SELECT * FROM study_sessions WHERE user_id = :userId AND date LIKE :prefix || '%' ORDER BY date")
    suspend fun getSessionsByMonth(userId: Int, prefix: String): List<StudySession>

    // Xóa tất cả session của 1 user (khi reset)
    @Query("DELETE FROM study_sessions WHERE user_id = :userId")
    suspend fun deleteSessionsByUserId(userId: Int)
}