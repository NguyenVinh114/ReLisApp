package com.example.relisapp.phat.dao

import androidx.room.*
import com.example.relisapp.phat.entity.Notifications

@Dao
interface NotificationDao {
    @Query("SELECT * FROM Notifications")
    suspend fun getAll(): List<Notifications>

    @Insert
    suspend fun insert(notifications: Notifications)
}