package com.example.familyguardapp.services

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.content.edit

// Service checks UI once in a minute and captures text from it
class ScreenScanningService : AccessibilityService() {

    private var currentPackage : String = ""

    private val handler = Handler(Looper.getMainLooper())
    private val intervalMillis = 60_000L

    // object that encapsulates periodic UI check
    private val periodicTask = object : Runnable {
        override fun run() {
            performUiCheck()
            handler.postDelayed(this, intervalMillis)
        }
    }

    // UI checks start as soon as service is connected (permission is given)
    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("ScreenScanner", "Accessibility service connected")
        handler.postDelayed(periodicTask, intervalMillis)
    }

    // just necessary callback, not needed
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            currentPackage = if (event.packageName != null)
                event.packageName.toString() else ""
        }
    }

    // interruption callback
    override fun onInterrupt() {
        Log.d("ScreenScanner", "Accessibility service interrupted")
        handler.removeCallbacks(periodicTask)
    }

    // destruction callback
    override fun onDestroy() {
        Log.d("ScreenScanner", "Accessibility service destroyed")
        handler.removeCallbacks(periodicTask)
        super.onDestroy()
    }

    // checks UI and wrap captured text and currently opened app package into SharedPreferences
    private fun performUiCheck() {
        val root = rootInActiveWindow ?: return

        val textList = mutableListOf<String>()
        collectText(root, textList)
        val capturedText = textList.joinToString(" ")

        var appName = getAppName(this, currentPackage)
        if (appName.isEmpty()) {
            appName = if (isSystemApp(this, currentPackage)) {
                "System app"
            } else {
                "Unknown app"
            }
        }

        val prefs = getSharedPreferences("scanner_data", MODE_PRIVATE)
        prefs.edit {
            putString("captured_text", capturedText)
            putString("source_app", appName)
        }
        Log.d("FamScan", "Name: $appName, package: $currentPackage")
    }

    // collects text from root view and all children views
    private fun collectText(node: AccessibilityNodeInfo, allTexts: MutableList<String>) {
        node.text?.toString()?.let {
            if (it.isNotBlank()) allTexts.add(it)
        }

        for (i in 0 until node.childCount) {
            node.getChild(i)?.let {
                collectText(it, allTexts)
            }
        }
    }

    private fun getAppName(context : Context, appPackage : String) : String {
        try {
            val pacMan = context.packageManager
            val appInfo = pacMan.getApplicationInfo(appPackage, 0)
            val appName = pacMan.getApplicationLabel(appInfo).toString()
            return appName
        } catch (e : PackageManager.NameNotFoundException) {
            Log.d("FamScan", "package: $appPackage, name exception")
            return ""
        }
    }

    private fun isSystemApp(context : Context, appPackage : String) : Boolean {
        try {
            val pacMan = context.packageManager
            val appInfo = pacMan.getApplicationInfo(appPackage, 0)
            val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            val isUpdatedSystem = (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
            return isSystem || isUpdatedSystem
        } catch (e : PackageManager.NameNotFoundException) {
            return false
        }
    }
}
