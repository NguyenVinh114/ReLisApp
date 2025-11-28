package com.example.relisapp.nam.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.nam.worker.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReminderSettingsUiState(
    val isReminderEnabled: Boolean = false,
    val reminderHour: Int = 20,
    val reminderMinute: Int = 0,
    val isPermissionGranted: Boolean = false,
    val isScheduled: Boolean = false
)

class ReminderSettingsViewModel(
    private val context: Context,
    private val scheduler: ReminderScheduler
) : ViewModel() {

    private val prefs = context.getSharedPreferences("relis_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(ReminderSettingsUiState())
    val uiState: StateFlow<ReminderSettingsUiState> = _uiState

    init {
        loadSettings()
        checkPermission()
        checkScheduleStatus()
    }

    private fun loadSettings() {
        val enabled = prefs.getBoolean("reminder_enabled", false)
        val hour = prefs.getInt("reminder_hour", 20)
        val minute = prefs.getInt("reminder_minute", 0)

        _uiState.update {
            it.copy(
                isReminderEnabled = enabled,
                reminderHour = hour,
                reminderMinute = minute
            )
        }
    }

    private fun checkPermission() {
        val granted =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }

        _uiState.update { it.copy(isPermissionGranted = granted) }
    }

    private fun checkScheduleStatus() {
        val status = scheduler.isReminderScheduled()
        _uiState.update { it.copy(isScheduled = status) }
    }

    fun toggleReminder(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled && !_uiState.value.isPermissionGranted) {
                // UI sẽ tự xin permission
                return@launch
            }

            prefs.edit().putBoolean("reminder_enabled", enabled).apply()

            _uiState.update { it.copy(isReminderEnabled = enabled) }

            if (enabled) {
                scheduler.scheduleDailyReminder(
                    _uiState.value.reminderHour,
                    _uiState.value.reminderMinute
                )
            } else {
                scheduler.cancelReminder()
            }

            checkScheduleStatus()
        }
    }

    fun setReminderTime(hour: Int, minute: Int) {
        prefs.edit()
            .putInt("reminder_hour", hour)
            .putInt("reminder_minute", minute)
            .apply()

        _uiState.update {
            it.copy(reminderHour = hour, reminderMinute = minute)
        }

        if (_uiState.value.isReminderEnabled) {
            scheduler.scheduleDailyReminder(hour, minute)
            checkScheduleStatus()
        }
    }

    fun onPermissionResult(granted: Boolean) {
        _uiState.update { it.copy(isPermissionGranted = granted) }

        if (granted && _uiState.value.isReminderEnabled) {
            toggleReminder(true)
        }
    }

    fun getReminderTimeString(): String {
        return "%02d:%02d".format(
            _uiState.value.reminderHour,
            _uiState.value.reminderMinute
        )
    }
}
