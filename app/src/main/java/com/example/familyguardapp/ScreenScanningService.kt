package com.example.familyguardapp

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.localbroadcastmanager.content.LocalBroadcastManager

// Service checks UI once in a minute and captures text from it
class ScreenScanningService : AccessibilityService() {

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
            val packageName = event.packageName?.toString()
            val className = event.className?.toString()

            // checking for openin admin device settings page
            if (packageName == "com.android.settings" &&
                (className?.contains("DeviceAdmin") == true || className?.contains("Admin") == true)) {
                val intent = Intent(this, BlockingActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
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

        val currentPackageName = root.packageName?.toString()

        val prefs = getSharedPreferences("scanner_data", MODE_PRIVATE)
        prefs.edit().putString("captured_text", capturedText).apply()
        prefs.edit().putString("source_app", currentPackageName).apply()
    }

    // collects text from root view and all children views
    private fun collectText(node : AccessibilityNodeInfo, allTexts : MutableList<String>) {
        node.text?.toString()?.let {
            if (it.isNotBlank()) allTexts.add(it)
        }

        for (i in 0..<node.childCount) {
            node.getChild(i)?.let {
                collectText(it, allTexts)
            }
        }
    }
}
