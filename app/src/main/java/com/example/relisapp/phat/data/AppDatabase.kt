package com.example.relisapp.phat.data

import android.content.Context
import android.util.Log
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
    version = 1
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
        private const val DATABASE_NAME = "relis_database" // Đặt tên DB vào một hằng số

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Sử dụng double-checked locking để đảm bảo an toàn và hiệu quả
            return INSTANCE ?: synchronized(this) {
                val instance = buildDatabase(context) // Gọi hàm build đã được tách ra
                INSTANCE = instance
                instance
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            val dbFile = context.getDatabasePath(DATABASE_NAME)

            val builder = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )

            if (!dbFile.exists()) {
                // [QUAN TRỌNG] Chỉ gọi createFromAsset KHI VÀ CHỈ KHI file DB chưa tồn tại
                builder.createFromAsset("DB_ReLis_V2.db")
                // Log để biết rằng DB được tạo từ Asset
                Log.i("AppDatabase", "Database created from asset.")
            } else {
                // Log để biết rằng DB đã tồn tại và được mở lại
                Log.i("AppDatabase", "Existing database opened.")
            }

            // Bạn vẫn có thể giữ fallbackToDestructiveMigration ở đây
            // Nó sẽ chỉ kích hoạt khi bạn tăng version và DB đã tồn tại
            builder.fallbackToDestructiveMigration(true)

            return builder.build()
        }
    }
}
