package com.example.relisapp.phat.dao

import androidx.room.*
import com.example.relisapp.phat.entity.Categories

@Dao
interface CategoryDao {
    @Query("SELECT * FROM Categories where isLocked = 0 ORDER BY categoryName ASC")
    suspend fun getAllForUser(): List<Categories>

    @Query("SELECT * FROM Categories ORDER BY categoryName ASC")
    suspend fun getAll(): List<Categories>

    @Query("SELECT * FROM Categories WHERE categoryId = :id")
    suspend fun getById(id: Int): Categories?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categories: Categories)

    @Update
    suspend fun update(categories: Categories)

    @Delete
    suspend fun delete(categories: Categories)

    @Query("SELECT * FROM Categories WHERE categoryName = :name AND type = :type LIMIT 1")
    suspend fun findByNameAndType(name: String, type: String): Categories?
}
