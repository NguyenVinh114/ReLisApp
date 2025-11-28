package com.example.relisapp.nam.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.relisapp.nam.data.repository.UserRepository
import com.example.relisapp.nam.database.AppDatabase
import com.example.relisapp.nam.logic.StreakManager

class DailyReminderWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // 1. Lấy database instance
            val database = AppDatabase.getDatabase(context)

            // 2. Khởi tạo các dependency cần thiết cho StreakManager
            // ⭐ [FIX] Tạo thêm UserRepository từ UserDao
            val userRepository = UserRepository(database.userDao())

            // ⭐ [FIX] Truyền đủ 2 tham số: StudySessionDao và UserRepository
            val streakManager = StreakManager(
                studySessionDao = database.studySessionDao(),
                userRepository = userRepository
            )

            // 3. NotificationHelper
            val notificationHelper = NotificationHelper(context)

            // 4. Lấy dữ liệu streak của user hiện tại (Logic cũ cần chỉnh lại chút)
            // Lưu ý: Worker chạy ngầm nên không biết ai đang login.
            // Ở đây ta tạm thời check user đăng nhập gần nhất hoặc chỉ hiển thị thông báo chung.
            // Để đơn giản và tránh lỗi logic phức tạp khi chạy nền, ta sẽ chỉ nhắc nhở chung
            // hoặc nếu muốn xịn hơn thì check SharedPref để lấy last logged in user ID.

            // Lấy User ID từ Session Manager (cần khởi tạo thủ công ở đây)
            val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
            val userId = prefs.getInt("user_id", -1)

            if (userId != -1) {
                // Nếu có user đang đăng nhập, check streak của user đó
                val currentStreak = streakManager.getCurrentStreak(userId)
                val isAtRisk = streakManager.isStreakAtRisk(userId)

                // 5. Gửi thông báo
                if (isAtRisk) {
                    notificationHelper.showNotification(
                        title = "🔥 Báo động: Streak $currentStreak ngày sắp mất!",
                        message = "Chỉ còn vài giờ nữa! Hãy vào học 5 phút để giữ chuỗi học tập nhé.",
                        isUrgent = true
                    )
                } else {
                    val motivational = listOf(
                        "Đến giờ ReLis rồi! Duy trì 5 phút mỗi ngày nhé!",
                        "Quyết tâm thêm chút nữa nào 🔥",
                        "Học 1 bài mới để tiến bộ mỗi ngày!",
                        "Giữ phong độ nhé! Vào ReLis học ngay!"
                    )

                    notificationHelper.showNotification(
                        title = "⏰ Đến giờ học rồi!",
                        message = motivational.random(),
                        isUrgent = false
                    )
                }
            } else {
                // Nếu không tìm thấy user ID (đã logout), gửi nhắc nhở chung hoặc không gửi
                notificationHelper.showNotification(
                    title = "⏰ Bạn ơi, đã lâu không gặp!",
                    message = "Vào học ngay để nâng cao trình độ nhé!",
                    isUrgent = false
                )
            }

            Result.success()

        } catch (e: Exception) {
            Log.e("DailyReminderWorker", "Lỗi Worker: ${e.message}")
            Result.failure()
        }
    }
}