package com.example.relisapp.nam.logic

import com.example.relisapp.nam.data.repository.UserRepository
import com.example.relisapp.nam.database.dao.StudySessionDao
import com.example.relisapp.nam.database.entity.StudySession
import com.example.relisapp.nam.model.StreakMilestone
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar // Dùng cho hàm createTestStreak cũ

class StreakManager(
    private val studySessionDao: StudySessionDao,
    private val userRepository: UserRepository
) {
    // Sử dụng LocalDate thay vì Date/SimpleDateFormat cũ
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE // Định dạng YYYY-MM-DD

    // Lấy ngày hôm nay dạng String chuẩn
    fun today(): String = LocalDate.now().format(dateFormatter)

    // ======================================================================
    // 1) GHI SESSION
    // ======================================================================
    suspend fun recordStudySession(
        userId: Int,
        lessonsCompleted: Int,
        isListening: Boolean,
        timeMinutes: Int,
        score: Float
    ) {
        val todayStr = today()
        val existing = studySessionDao.getSessionByDate(userId, todayStr)

        val session = if (existing == null) {
            StudySession(
                userId = userId,
                date = todayStr,
                lessonsCompleted = lessonsCompleted,
                listeningCount = if (isListening) 1 else 0,
                readingCount = if (!isListening) 1 else 0,
                totalTimeMinutes = timeMinutes,
                scoreAverage = score
            )
        } else {
            existing.copy(
                lessonsCompleted = existing.lessonsCompleted + lessonsCompleted,
                listeningCount = existing.listeningCount + (if (isListening) 1 else 0),
                readingCount = existing.readingCount + (if (!isListening) 1 else 0),
                totalTimeMinutes = existing.totalTimeMinutes + timeMinutes,
                scoreAverage = if(score > 0) (existing.scoreAverage + score)/2 else existing.scoreAverage
            )
        }

        if (existing == null) studySessionDao.insertSession(session)
        else studySessionDao.updateSession(session)

        syncUserStreak(userId)
    }

    private suspend fun syncUserStreak(userId: Int) {
        val current = calculateCurrentStreak(userId)
        val longest = calculateLongestStreak(userId)
        userRepository.updateUserStreak(userId, current, longest, today())
    }

    // ======================================================================
    // 2) LOGIC TÍNH STREAK (Dùng LocalDate - Chính xác tuyệt đối)
    // ======================================================================

    suspend fun calculateCurrentStreak(userId: Int): Int {
        // Lấy 365 ngày gần nhất, sắp xếp giảm dần (mới nhất trước)
        val sessions = studySessionDao.getRecentSessions(userId, 365)
            .filter { it.lessonsCompleted > 0 } // Chỉ tính ngày có học
            .sortedByDescending { it.date }

        if (sessions.isEmpty()) return 0

        var streak = 0
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        // Parse ngày học gần nhất của user
        // Cần try-catch phòng trường hợp date cũ trong DB sai định dạng (nếu có)
        val lastStudyDate = try {
            LocalDate.parse(sessions[0].date, dateFormatter)
        } catch (e: Exception) {
            return 0
        }

        // Nếu ngày học gần nhất không phải hôm nay hoặc hôm qua -> Mất chuỗi -> Streak = 0
        if (!lastStudyDate.isEqual(today) && !lastStudyDate.isEqual(yesterday)) {
            return 0
        }

        // Bắt đầu đếm
        var expectedDate = lastStudyDate

        for (session in sessions) {
            val sessionDate = try {
                LocalDate.parse(session.date, dateFormatter)
            } catch (e: Exception) {
                continue
            }

            if (sessionDate.isEqual(expectedDate)) {
                // Ngày này khớp với ngày mong đợi -> Tăng streak
                streak++
                // Ngày mong đợi tiếp theo là ngày trước đó (lùi 1 ngày)
                expectedDate = expectedDate.minusDays(1)
            } else {
                // Có lỗ hổng (gap) -> Dừng đếm ngay lập tức
                break
            }
        }
        return streak
    }

    suspend fun calculateLongestStreak(userId: Int): Int {
        val sessions = studySessionDao.getRecentSessions(userId, 365)
            .filter { it.lessonsCompleted > 0 }
            .sortedBy { it.date } // Sắp xếp tăng dần (cũ -> mới)

        if (sessions.isEmpty()) return 0

        var maxStreak = 0
        var currentStreak = 1

        if (sessions.size == 1) return 1

        for (i in 0 until sessions.size - 1) {
            val dateCurrent = LocalDate.parse(sessions[i].date, dateFormatter)
            val dateNext = LocalDate.parse(sessions[i+1].date, dateFormatter)

            val daysDiff = ChronoUnit.DAYS.between(dateCurrent, dateNext)

            if (daysDiff == 1L) {
                currentStreak++
            } else if (daysDiff > 1L) {
                if (currentStreak > maxStreak) maxStreak = currentStreak
                currentStreak = 1
            }
        }

        if (currentStreak > maxStreak) maxStreak = currentStreak
        return maxStreak
    }

    // ======================================================================
    // 3) CÁC HÀM GETTER & HELPER (Đã khôi phục lại để fix lỗi Build)
    // ======================================================================

    suspend fun getCurrentStreak(userId: Int): Int = calculateCurrentStreak(userId)

    suspend fun getLongestStreak(userId: Int): Int = calculateLongestStreak(userId)

    suspend fun isStudiedToday(userId: Int): Boolean {
        val todayStr = LocalDate.now().format(dateFormatter)
        val session = studySessionDao.getSessionByDate(userId, todayStr)
        return session != null && session.lessonsCompleted > 0
    }

    // ⭐ Khôi phục hàm này:
    suspend fun getTotalDays(userId: Int): Int = studySessionDao.getTotalDays(userId)

    // ⭐ Khôi phục hàm này:
    suspend fun isStreakAtRisk(userId: Int): Boolean {
        // Có streak (>0) nhưng hôm nay chưa học (!isStudiedToday)
        return getCurrentStreak(userId) > 0 && !isStudiedToday(userId)
    }

    // ⭐ Khôi phục hàm này:
    suspend fun getRecentSessions(userId: Int): List<StudySession> {
        return studySessionDao.getRecentSessions(userId, 30)
    }

    // ⭐ Khôi phục hàm này:
    suspend fun getSessionsByMonth(userId: Int, prefix: String): List<StudySession> {
        // Prefix dạng "YYYY-MM-" để search SQL LIKE
        return studySessionDao.getSessionsByMonth(userId, "$prefix%")
    }

    // ⭐ Khôi phục hàm này:
    suspend fun checkMilestone(userId: Int): StreakMilestone? {
        val streak = getCurrentStreak(userId)
        // Tìm milestone có số ngày TRÙNG KHỚP với streak hiện tại
        return StreakMilestone.entries.firstOrNull { it.days == streak }
    }

    // ⭐ Khôi phục hàm này:
    suspend fun getNextMilestone(userId: Int): StreakMilestone? {
        val streak = getCurrentStreak(userId)
        // Tìm milestone tiếp theo lớn hơn streak hiện tại
        return StreakMilestone.entries.firstOrNull { it.days > streak }
    }

    // ⭐ Khôi phục hàm Test (dù không dùng UI nhưng để tránh lỗi biên dịch nếu còn gọi)
    suspend fun createTestStreak(userId: Int, days: Int) {
        val today = LocalDate.now()
        repeat(days) { i ->
            val date = today.minusDays(i.toLong()).format(dateFormatter)
            val session = StudySession(
                userId = userId,
                date = date,
                lessonsCompleted = 1,
                listeningCount = 1,
                totalTimeMinutes = 10,
                scoreAverage = 80f
            )
            // Insert or Replace
            // Lưu ý: Logic này insert thô, thực tế nên check exist
            studySessionDao.insertSession(session)
        }
        syncUserStreak(userId)
    }
}