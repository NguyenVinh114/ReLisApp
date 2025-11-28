package com.example.relisapp.nam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.nam.data.local.SessionManager
import com.example.relisapp.nam.logic.StreakManager
import com.example.relisapp.nam.model.StreakMilestone
import com.example.relisapp.nam.database.entity.StudySession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// UI State giữ nguyên
data class StreakUiState(
    val isLoading: Boolean = true,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalDays: Int = 0,
    val isStudiedToday: Boolean = false,
    val isStreakAtRisk: Boolean = false,
    val nextMilestone: StreakMilestone? = null,
    val recentSessions: List<StudySession> = emptyList(),
    val achievedMilestones: List<StreakMilestone> = emptyList(),
    val showMilestoneDialog: StreakMilestone? = null,
    val calendarSessions: List<StudySession> = emptyList() // For calendar
)

class StreakViewModel(
    private val streakManager: StreakManager,
    private val sessionManager: SessionManager // ⭐ Inject SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(StreakUiState())
    val uiState: StateFlow<StreakUiState> = _uiState

    // Lấy userId hiện tại
    private val currentUserId: Int
        get() = sessionManager.getUserId()

    init {
        refreshData()
    }

    fun refreshData() {
        val userId = currentUserId
        if (userId == -1) return // Chưa login

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val current = streakManager.getCurrentStreak(userId)
            val longest = streakManager.getLongestStreak(userId)
            val total = streakManager.getTotalDays(userId)
            val studiedToday = streakManager.isStudiedToday(userId)
            val atRisk = streakManager.isStreakAtRisk(userId)
            val next = streakManager.getNextMilestone(userId)
            val recent = streakManager.getRecentSessions(userId) // 30 days

            val achieved = StreakMilestone.entries.filter { it.days <= current }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    currentStreak = current,
                    longestStreak = longest,
                    totalDays = total,
                    isStudiedToday = studiedToday,
                    isStreakAtRisk = atRisk,
                    nextMilestone = next,
                    recentSessions = recent,
                    achievedMilestones = achieved
                )
            }
        }
    }

    // Gọi khi hoàn thành bài học
    fun onLessonCompleted(score: Float, isListening: Boolean) {
        val userId = currentUserId
        if (userId == -1) return

        viewModelScope.launch {
            streakManager.recordStudySession(
                userId = userId,
                lessonsCompleted = 1,
                isListening = isListening,
                timeMinutes = 5,
                score = score
            )

            // Check milestone ngay lập tức
            val milestone = streakManager.checkMilestone(userId)

            // Refresh lại UI
            refreshData()

            if (milestone != null) {
                _uiState.update { it.copy(showMilestoneDialog = milestone) }
            }
        }
    }

    fun dismissMilestoneDialog() {
        _uiState.update { it.copy(showMilestoneDialog = null) }
    }

    // Calendar logic
    fun loadMonth(year: Int, month: Int) {
        val userId = currentUserId
        if (userId == -1) return

        viewModelScope.launch {
            val prefix = "%04d-%02d".format(year, month)
            val list = streakManager.getSessionsByMonth(userId, prefix)
            _uiState.update { it.copy(calendarSessions = list) }
        }
    }

    // Debug tool
    fun testCreateFakeStreak(days: Int) {
        val userId = currentUserId
        if (userId == -1) return
        viewModelScope.launch {
            streakManager.createTestStreak(userId, days)
            refreshData()
        }
    }
}