package com.example.relisapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.relisapp.data.local.dao.ResultDao
import com.example.relisapp.data.local.entity.model.ResultWithLessonInfo
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
private const val TEMP_USER_ID = 6
class ProgressViewModel(private val resultDao: ResultDao) : ViewModel() {

    private val _studyHistory = MutableStateFlow<List<ResultWithLessonInfo>>(emptyList())
    val studyHistory: StateFlow<List<ResultWithLessonInfo>> = _studyHistory

    private val _listeningChartEntries = MutableStateFlow<List<FloatEntry>>(emptyList())
    val listeningChartEntries = _listeningChartEntries

    private val _readingChartEntries = MutableStateFlow<List<FloatEntry>>(emptyList())
    val readingChartEntries = _readingChartEntries

    // Labels riêng cho từng kỹ năng
    private val _listeningLabels = MutableStateFlow<List<String>>(emptyList())
    val listeningLabels = _listeningLabels

    private val _readingLabels = MutableStateFlow<List<String>>(emptyList())
    val readingLabels = _readingLabels

    fun loadRealData(userId: Int) {

        // A. Lịch sử làm bài
        viewModelScope.launch {
            resultDao.getAllHistory(userId).collect { historyList ->
                _studyHistory.value = historyList
            }
        }

        // B. Listening chart
        viewModelScope.launch {
            resultDao.getChartData(userId, "listening").collect { data ->
                _listeningChartEntries.value = processChartData(data)
                _listeningLabels.value = data.map { it.createdAt }
            }
        }

        // C. Reading chart
        viewModelScope.launch {
            resultDao.getChartData(userId, "reading").collect { data ->
                _readingChartEntries.value = processChartData(data)
                _readingLabels.value = data.map { it.createdAt }
            }
        }
    }

    private fun processChartData(data: List<ResultWithLessonInfo>): List<FloatEntry> {
        return data.mapIndexed { index, r ->
            val score = r.score ?: 0
            val total = r.totalQuestions ?: 1
            val chartValue = (score.toFloat() / total) * 10f
            entryOf(index.toFloat(), chartValue)
        }
    }

    class Factory(private val resultDao: ResultDao) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ProgressViewModel(resultDao) as T
    }
}
