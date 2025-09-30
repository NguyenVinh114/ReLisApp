package com.example.relisapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.relisapp.model.*

@Database(
    entities = [
        User::class,
        Lesson::class,
        Question::class,
        Result::class,
        Favorite::class,
        Feedback::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun lessonDao(): LessonDao
    abstract fun questionDao(): QuestionDao
    abstract fun resultDao(): ResultDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun feedbackDao(): FeedbackDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "db_app"  // 👈 database name
                )
                    .fallbackToDestructiveMigration() // Xóa DB cũ nếu thay đổi schema
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
