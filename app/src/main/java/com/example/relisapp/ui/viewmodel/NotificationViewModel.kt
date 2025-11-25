package com.example.relisapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.relisapp.data.local.entity.Notifications
import com.example.relisapp.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(private val repo: NotificationRepository) : ViewModel() {

    private val _notifications = MutableStateFlow<List<Notifications>>(emptyList())
    val notifications: StateFlow<List<Notifications>> = _notifications

    fun loadNotifications() {
        viewModelScope.launch {
            _notifications.value = repo.getNotifications()
        }
    }

    fun addNotification(notifications: Notifications) {
        viewModelScope.launch {
            repo.addNotification(notifications)
            loadNotifications()
        }
    }
}
