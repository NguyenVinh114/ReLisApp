package com.example.relisapp.phat.dao

import androidx.room.*
import com.example.relisapp.phat.entity.Choices

@Dao
interface ChoiceDao {
    @Query("SELECT * FROM Choices")
    suspend fun getAll(): List<Choices>

    @Insert
    suspend fun insert(choices: Choices)
}
