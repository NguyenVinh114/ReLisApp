package com.example.relisapp.phat.data

import android.content.Context
import androidx.room.*
import com.example.relisapp.phat.dao.CategoryDao
import com.example.relisapp.phat.dao.ChoiceDao
import com.example.relisapp.phat.dao.CommentDao
import com.example.relisapp.phat.dao.FavoriteLessonDao
import com.example.relisapp.phat.dao.LessonDao
import com.example.relisapp.phat.dao.LikeDao
import com.example.relisapp.phat.dao.NotificationDao
import com.example.relisapp.phat.dao.ProgressDao
import com.example.relisapp.phat.dao.QuestionDao
import com.example.relisapp.phat.dao.ResultDao
import com.example.relisapp.phat.dao.UserDao
import com.example.relisapp.phat.entity.Categories
import com.example.relisapp.phat.entity.Choices
import com.example.relisapp.phat.entity.Comments
import com.example.relisapp.phat.entity.FavoriteLessons
import com.example.relisapp.phat.entity.Lessons
import com.example.relisapp.phat.entity.Likes
import com.example.relisapp.phat.entity.Notifications
import com.example.relisapp.phat.entity.Progress
import com.example.relisapp.phat.entity.Questions
import com.example.relisapp.phat.entity.Users
import com.example.relisapp.phat.entity.Results

@Database(
    entities = [
        Users::class, Categories::class, Lessons::class, Questions::class,
        Choices::class, Results::class, Progress::class, FavoriteLessons::class,
        Likes::class, Comments::class, Notifications::class
    ],
    version = 2
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
                    .fallbackToDestructiveMigration(true) // Room sẽ recreate DB nếu schema không khớp
                    .build()
                INSTANCE = instance
                instance
            }
        }

    }
}
