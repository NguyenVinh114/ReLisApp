package com.example.relisapp.phat.repository

import com.example.relisapp.phat.dao.NotificationDao
import com.example.relisapp.phat.entity.Notifications

class NotificationRepository(private val notificationDao: NotificationDao) {
    suspend fun getNotifications(): List<Notifications> = notificationDao.getAll()
    suspend fun addNotification(notifications: Notifications) = notificationDao.insert(notifications)
}
