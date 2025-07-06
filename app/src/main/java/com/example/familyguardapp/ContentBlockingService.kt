package com.example.familyguardapp

import android.accessibilityservice.AccessibilityService
import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.accessibility.AccessibilityEvent
import android.view.View
import android.view.WindowManager
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Button
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class ContentBlockingService : AccessibilityService() {

    //Variables for blocking
    private lateinit var windowManager : WindowManager
    private var overlayLayout : View? = null
    private var currentPackage : String? = null
    private val unblockable = listOf("com.example.familyguardapp",
        "com.google.android.apps.nexuslauncher")
    private val blockList = mutableSetOf<String>()

    //Variables for app removing from the blocklist after timer has expired
    private val appPollingThread = HandlerThread("AppPollingThread")
    .apply {start()}
    private val appPollHandler = Handler(appPollingThread.looper)
    private val uiHandler = Handler(Looper.getMainLooper())
    private val removalRunnables = mutableMapOf<String,  Runnable>()
    private val pollInterval = 10_000L
    private val removeInterval = 120_000L
    private val removeIntervalInMinutes = (removeInterval/ 60_000L).toInt()

    //Broadcast receiver that triggers showing overlay
    //when receiving signal from ForegroundDispatchService
    private val overlayTriggerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == OVERLAY_TRIGGER_ACTION) {
                if (currentPackage !in unblockable) {
                    showOverlay()
                }
            }
        }
    }

    //Runnable that checks currently running apps every 10 seconds
    val appPollRunnable = object : Runnable {
        override fun run() {
            checkRunningApps()
            appPollHandler.postDelayed(this, pollInterval)
        }
    }

    //Checks currently running apps. If any app from blocklist stopped running
    //and no timer is assigned to it, timer for this app starts
    private fun checkRunningApps() {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningApps = activityManager.runningAppProcesses
            ?.map {it.processName} ?: emptyList()

        blockList.forEach { packageName ->
            if (packageName !in runningApps &&
                !removalRunnables.containsKey(packageName)) {
                startRemovalTimer(packageName)
            }
        }
    }

    //Creates a timer for an app from the blocklist
    private fun startRemovalTimer(packageName : String) {
        val timer = Runnable {
            blockList.remove(packageName)
            removalRunnables.remove(packageName)
            Log.d(TAG, "App $packageName removed from the blocklist")
        }

        removalRunnables[packageName] = timer
        uiHandler.postDelayed(timer, removeInterval)
        Log.d(TAG, "Started $removeIntervalInMinutes-minute timer for $packageName")
    }

    //Shows blocking overlay
    private fun showOverlay() {
        if (overlayLayout != null) {
            return
        }
        Log.d(TAG, "Showing overlay for $currentPackage")
        currentPackage?.let { blockList.add(it) }

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val inflater = LayoutInflater.from(this)
        overlayLayout = inflater.inflate(R.layout.overlay_blocking, null)

        overlayLayout?.findViewById<Button>(R.id.close_button)?.setOnClickListener {
            val goHomeMenuIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(goHomeMenuIntent)
            removeOverlay()
        }

        val parameters = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.OPAQUE
        )
        try {
            windowManager.addView(overlayLayout, parameters)
        } catch (e : Exception) {
            Log.d(TAG, "Error while trying to draw overlay")
            overlayLayout = null
        }
    }

    //Removes blocking overlay
    private fun removeOverlay() {
        if (overlayLayout != null) {
            windowManager.removeView(overlayLayout)
            overlayLayout = null
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        val filter = IntentFilter(OVERLAY_TRIGGER_ACTION)
        LocalBroadcastManager.getInstance(this).registerReceiver(
            overlayTriggerReceiver, filter)
        Log.d(TAG, "Service connected")

        startPolling()
        Log.d(TAG, "Started polling apps")
    }

    //Starts execution of appPollRunnable
    private fun startPolling() {
        appPollHandler.post(appPollRunnable)
    }

    //Triggers showing overlay when app from the blocklist comes to the foreground
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        currentPackage = event?.packageName.toString()
        val className = event?.className.toString()
        Log.d(TAG, "package: $currentPackage, class: $className")
        if (blockList.contains(currentPackage)) {
            showOverlay()
        }

        if( checkDeviceAdminDisableAttempt(event)) {
            startBlockingActivity()
        }
    }

    private fun checkDeviceAdminDisableAttempt(event : AccessibilityEvent?) : Boolean {
        val packageName = event?.packageName.toString()
        val className = event?.className.toString()
        val rightPackageAndClass = packageName == "com.android.settings" &&
                (className.contains("DeviceAdminAdd") || checkAlertScreen(event))

        if (rightPackageAndClass) {
            return checkDeviceAdminStatus()
        }
        return false
    }

    private fun checkDeviceAdminStatus() : Boolean {
        val devicePolicyManager = this.getSystemService(Context.DEVICE_POLICY_SERVICE)
                as DevicePolicyManager
        val adminComponent = ComponentName(this, MyDeviceAdminReceiver::class.java)
        return devicePolicyManager.isAdminActive(adminComponent)
    }

    private fun checkAlertScreen(event : AccessibilityEvent?) : Boolean {
        if (event?.className.toString().contains("AlertDialog")) {
            val rootNode = rootInActiveWindow ?: return false
            Log.d(TAG, "")
            return checkText(rootNode)
        }

        return false
    }

    private fun checkText(node : AccessibilityNodeInfo) : Boolean {
        val text = node.text
        if (text != null && text.contains("FamilyGuard App")) {
            Log.d(TAG + " text", "Found device text")
            return true
        }

        var found = false
        for (i in 0..<node.childCount) {
            if (checkText(node.getChild(i))) {
                found = true
                break
            }
        }

        return found
    }

    private fun startBlockingActivity() {
        val intent = Intent(this, BlockingActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
            overlayTriggerReceiver)
        appPollHandler.removeCallbacks(appPollRunnable)
        appPollingThread.quit()
        removalRunnables.values.forEach { uiHandler.removeCallbacks(it) }
        super.onDestroy()
    }

    companion object {
        private const val TAG = "ContentBlockingService"
        const val OVERLAY_TRIGGER_ACTION =
            "OVERLAY_TRIGGER_ACTION"
    }
}