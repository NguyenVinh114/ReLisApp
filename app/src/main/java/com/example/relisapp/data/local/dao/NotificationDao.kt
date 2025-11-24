package com.example.relisapp.data.local.dao

import androidx.room.*
import com.example.relisapp.data.local.entity.Notifications

@Dao
interface NotificationDao {
    @Query("SELECT * FROM Notifications")
    suspend fun getAll(): List<Notifications>

    @Insert
    suspend fun insert(notifications: Notifications)
}