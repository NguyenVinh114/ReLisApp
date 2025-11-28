package com.example.relisapp.nam.worker

import android.content.Context
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

class ReminderScheduler(private val context: Context) {

    fun scheduleDailyReminder(hour: Int = 20, minute: Int = 0) {
        val workManager = WorkManager.getInstance(context)

        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        val initialDelay = target.timeInMillis - now.timeInMillis

        val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag("daily_reminder_tag")
            .build()

        workManager.enqueueUniquePeriodicWork(
            "daily_reminder_work",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    fun cancelReminder() {
        WorkManager.getInstance(context)
            .cancelAllWorkByTag("daily_reminder_tag")
    }

    fun isReminderScheduled(): Boolean {
        val workManager = WorkManager.getInstance(context)

        val workInfos = workManager.getWorkInfosByTag("daily_reminder_tag").get()

        return workInfos.any { info ->
            info.state == WorkInfo.State.ENQUEUED ||
                    info.state == WorkInfo.State.RUNNING
        }
    }
}
