package com.example.relisapp.nam.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.relisapp.nam.viewmodel.ReminderSettingsViewModel
import com.example.relisapp.nam.worker.ReminderScheduler

class ReminderSettingsViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReminderSettingsViewModel::class.java)) {
            return ReminderSettingsViewModel(
                context = context.applicationContext,
                scheduler = ReminderScheduler(context.applicationContext)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
