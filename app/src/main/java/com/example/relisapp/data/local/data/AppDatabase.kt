package com.example.relisapp.data.local.data

import android.content.Context
import androidx.room.*
import com.example.relisapp.data.local.dao.CategoryDao
import com.example.relisapp.data.local.dao.ChoiceDao
import com.example.relisapp.data.local.dao.CommentDao
import com.example.relisapp.data.local.dao.FavoriteLessonDao
import com.example.relisapp.data.local.dao.LessonDao
import com.example.relisapp.data.local.dao.LikeDao
import com.example.relisapp.data.local.dao.NotificationDao
import com.example.relisapp.data.local.dao.ProgressDao
import com.example.relisapp.data.local.dao.QuestionDao
import com.example.relisapp.data.local.dao.ResultDao
import com.example.relisapp.data.local.dao.UserDao
import com.example.relisapp.data.local.entity.Categories
import com.example.relisapp.data.local.entity.Choices
import com.example.relisapp.data.local.entity.Comments
import com.example.relisapp.data.local.entity.FavoriteLessons
import com.example.relisapp.data.local.entity.Lessons
import com.example.relisapp.data.local.entity.Likes
import com.example.relisapp.data.local.entity.Notifications
import com.example.relisapp.data.local.entity.Progress
import com.example.relisapp.data.local.entity.Questions
import com.example.relisapp.data.local.entity.Users
import com.example.relisapp.data.local.entity.Results

@Database(
    entities = [
        Users::class, Categories::class, Lessons::class, Questions::class,
        Choices::class, Results::class, Progress::class, FavoriteLessons::class,
        Likes::class, Comments::class, Notifications::class
    ],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun lessonDao(): LessonDao
    abstract fun questionDao(): QuestionDao
    abstract fun choiceDao(): ChoiceDao
    abstract fun resultDao(): ResultDao
    abstract fun progressDao(): ProgressDao
    abstract fun favoriteLessonDao(): FavoriteLessonDao
    abstract fun likeDao(): LikeDao
    abstract fun commentDao(): CommentDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "relis_database" // tên database trong app
                )
                    .createFromAsset("DB_ReLis_V1.db") // file trong assets/
                 //   .fallbackToDestructiveMigration(true) // Room sẽ recreate DB nếu schema không khớp
                    .build()
                INSTANCE = instance
                instance
            }
        }

    }
}
