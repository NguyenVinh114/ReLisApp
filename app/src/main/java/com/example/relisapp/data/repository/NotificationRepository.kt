package com.example.relisapp.data.repository

import com.example.relisapp.data.local.dao.NotificationDao
import com.example.relisapp.data.local.entity.Notifications

class NotificationRepository(private val notificationDao: NotificationDao) {
    suspend fun getNotifications(): List<Notifications> = notificationDao.getAll()
    suspend fun addNotification(notifications: Notifications) = notificationDao.insert(notifications)
}
