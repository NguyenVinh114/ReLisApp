package com.example.relisapp.data.local.dao

import androidx.room.*
import com.example.relisapp.data.local.entity.FavoriteLessons
import com.example.relisapp.data.local.entity.Lessons
import kotlinx.coroutines.flow.Flow // Cần import này

@Dao
interface FavoriteLessonDao {
    @Query("SELECT * FROM FavoriteLessons")
    suspend fun getAll(): List<FavoriteLessons>
    @Query("SELECT * FROM FavoriteLessons WHERE userId = :userId")
    fun getFavoritesByUserId(userId: Int): Flow<List<FavoriteLessons>>

    @Query("""
        SELECT L.* FROM Lessons L
        INNER JOIN FavoriteLessons F
        ON L.lessonId = F.lessonId
        WHERE F.userId = :userId
    """)
    fun getFavoriteLessonsDetail(userId: Int): Flow<List<Lessons>>

    @Query("SELECT * FROM FavoriteLessons WHERE userId = :userId AND lessonId = :lessonId")
    suspend fun getFavorite(userId: Int, lessonId: Int): FavoriteLessons?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavorite(favorite: FavoriteLessons)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteLessons)

    @Insert
    suspend fun insert(favoriteLessons: FavoriteLessons)
}