package com.example.relisapp.nam.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.relisapp.nam.database.dao.CommentDao
import com.example.relisapp.nam.database.dao.LessonDao
import com.example.relisapp.nam.database.dao.LikeDao
import com.example.relisapp.nam.database.dao.UserDao
import com.example.relisapp.nam.database.entity.User
import com.example.relisapp.nam.database.entity.Comments
import com.example.relisapp.nam.database.entity.Lessons
import com.example.relisapp.nam.database.entity.Categories
import com.example.relisapp.nam.database.entity.Likes

@Database(
    entities = [
        User::class,
        Comments::class,
        Lessons::class,
        Categories::class,
        Likes::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun lessonDao(): LessonDao
    abstract fun likeDao(): LikeDao
    abstract fun commentDao(): CommentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "DB_ReLis_V1.db"
                )
                    .createFromAsset("DB_ReLis_V1.db")

                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}