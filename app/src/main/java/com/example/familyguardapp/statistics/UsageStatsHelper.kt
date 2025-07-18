package com.example.familyguardapp.statistics

import android.app.usage.UsageStatsManager
import android.content.Context
import com.example.familyguardapp.services.AppUsage

object UsageStatsHelper {
    fun getDailyUsageStats(context: Context): Pair<Int, List<AppUsage>> {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val startTime = endTime - 24 * 60 * 60 * 1000 // 24h

        val usageStatsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )

        var totalTime = 0L
        val appUsages = mutableListOf<AppUsage>()

        usageStatsList?.forEach {
            val minutes = (it.totalTimeInForeground / 1000 / 60).toInt()
            if (minutes > 0) {
                totalTime += minutes
                appUsages.add(AppUsage(it.packageName, minutes))
            }
        }

        val topApps = appUsages.sortedByDescending { it.minutes_used }.take(5)
        return Pair(totalTime.toInt(), topApps)
    }
}
