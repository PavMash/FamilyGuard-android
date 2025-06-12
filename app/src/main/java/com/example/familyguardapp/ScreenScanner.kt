package com.example.familyguardapp

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class ScreenScanner : AccessibilityService() {

    private val handler = Handler(Looper.getMainLooper())
    private val intervalMillis = 60_000L

    private val periodicTask = object : Runnable {
        override fun run() {
            performUiCheck()
            handler.postDelayed(this, intervalMillis)
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("ScreenScanner", "Accessibility service connected")
        handler.postDelayed(periodicTask, intervalMillis)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {
        handler.removeCallbacks(periodicTask)
    }

    override fun onDestroy() {
        handler.removeCallbacks(periodicTask)
        super.onDestroy()
    }

    private fun performUiCheck() {
        val root = rootInActiveWindow ?: return

        val allText = mutableListOf<String>()
        collectText(root, allText)
        //Now allText stores text captured from the screen.
        //You should work with it.
    }

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
