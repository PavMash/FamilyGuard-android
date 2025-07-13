package com.example.familyguardapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DailyReportReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("DailyReport", "Receiver triggered at 23:59")

        CoroutineScope(Dispatchers.IO).launch {
            val networkService = NetworkService(context)
            val (totalMinutes, topApps) = UsageStatsHelper.getDailyUsageStats(context)

            val childId = context.getSharedPreferences("childID", Context.MODE_PRIVATE)
                .getString("ID", "no child ID") ?: return@launch

            val report = UsageReport(
                child_id = childId,
                date = getTodayDateString(),
                total_screen_time_minutes = totalMinutes,
                top_apps = topApps
            )

            val result = networkService.sendUsageReport(report)
            scheduleNextAlarm(context)

            Log.d("DailyReport", result.toString())
        }
    }

    private fun scheduleNextAlarm(context: Context) {
        val alarmManager = ContextCompat.getSystemService(context, AlarmManager::class.java)
        if (alarmManager == null) {
            Log.e("DailyReport", "AlarmManager not available")
            return
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Log.w("DailyReport", "Exact alarm not permitted — skipping scheduling")
            return
        }

        val intent = Intent(context, DailyReportReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 0)
        }

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            Log.d("DailyReport", "Next alarm set for: ${calendar.time}")
        } catch (e: SecurityException) {
            Log.e("DailyReport", "Permission denied for exact alarm", e)
        }
    }




    private fun getTodayDateString(): String {
        val cal = java.util.Calendar.getInstance()
        return "%04d-%02d-%02d".format(
            cal.get(java.util.Calendar.YEAR),
            cal.get(java.util.Calendar.MONTH) + 1,
            cal.get(java.util.Calendar.DAY_OF_MONTH)
        )
    }
}
