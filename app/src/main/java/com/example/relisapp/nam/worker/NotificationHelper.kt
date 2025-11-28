package com.example.relisapp.nam.worker // Để tạm cùng package worker cho tiện

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
import com.example.relisapp.R // Nhớ import R của dự án bạn
import com.example.relisapp.nam.redirect.NotificationRedirectActivity

class NotificationHelper(private val context: Context) {
    companion object {
        const val CHANNEL_ID = "relis_daily_reminder"
    }

    init {
        createChannel()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Nhắc nhở học tập",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Thông báo nhắc học hàng ngày" }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun showNotification(title: String, message: String, isUrgent: Boolean) {
        // Kiểm tra permission Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) return
        }

        // Intent mở MainActivity khi click thông báo
        val intent = Intent(context, NotificationRedirectActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )


        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notif_white)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(1001, builder.build())
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

}