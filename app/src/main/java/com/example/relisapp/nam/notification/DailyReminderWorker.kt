package com.example.relisapp.nam.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.relisapp.nam.MainActivity
import com.example.relisapp.R

class DailyReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {

        val context = applicationContext
        val channelId = "daily_reminder_channel"

        // LOG KIỂM TRA WORKER CHẠY
        android.util.Log.d("DailyWorker", "Worker đang chạy!")

        // ✔ Kiểm tra quyền thông báo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                android.util.Log.e("DailyWorker", "Thiếu quyền POST_NOTIFICATIONS")
                return Result.failure()
            }
        }

        // ✔ Tạo NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Study Reminder",
                NotificationManager.IMPORTANCE_HIGH // Quan trọng để luôn hiện thông báo
            )
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        // ✔ Khi bấm thông báo → mở app
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // ✔ Tạo thông báo
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification) // phải là icon trắng-trong-suốt
            .setContentTitle("Luyện tập mỗi ngày!")
            .setContentText("Đã đến giờ học tiếng Anh rồi. Vào app luyện ngay!")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // ✔ Gửi thông báo
        NotificationManagerCompat.from(context).notify(1001, builder.build())

        return Result.success()
    }
}
