// package com.example.relisapp.phat.dao
package com.example.relisapp.phat.dao

import androidx.room.*
import com.example.relisapp.phat.entity.Categories

@Dao
interface CategoryDao {
    @Query("SELECT * FROM Categories ORDER BY categoryName ASC")
    suspend fun getAll(): List<Categories>

    // Thêm hàm lấy một category theo ID
    @Query("SELECT * FROM Categories WHERE categoryId = :id")
    suspend fun getById(id: Int): Categories?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categories: Categories)

    // Thêm hàm cập nhật
    @Update
    suspend fun update(categories: Categories)

    // Thêm hàm xóa
    @Delete
    suspend fun delete(categories: Categories)
}
