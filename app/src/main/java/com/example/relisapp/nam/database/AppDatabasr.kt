package com.example.relisapp.nam.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.relisapp.nam.database.dao.*
import com.example.relisapp.nam.database.entity.*

@Database(
    entities = [
        User::class,
        Categories::class,
        Lessons::class,
        Questions::class,       // Đã thêm
        Choices::class,         // Đã thêm
        Comments::class,
        Likes::class,
        FavoriteLessons::class, // Đã thêm
        Progress::class,        // Đã thêm
        Results::class,         // Đã thêm
        Notifications::class,   // Đã thêm
        StudySession::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun lessonDao(): LessonDao
    abstract fun likeDao(): LikeDao
    abstract fun commentDao(): CommentDao
    abstract fun studySessionDao(): StudySessionDao
    // Bạn nên thêm các DAO còn thiếu ở đây (ví dụ: questionDao, progressDao...)

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
                    // Chỉ dùng destructive migration khi đang dev,
                    // nó sẽ xoá dữ liệu user tạo ra nếu version thay đổi.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}