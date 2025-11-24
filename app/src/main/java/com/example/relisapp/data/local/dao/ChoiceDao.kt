package com.example.relisapp.data.local.dao

import androidx.room.*
import com.example.relisapp.data.local.entity.Choices

@Dao
interface ChoiceDao {
    @Query("SELECT * FROM Choices")
    suspend fun getAll(): List<Choices>

    @Insert
    suspend fun insert(choices: Choices)
}
