package com.example.familyguardapp

import android.Manifest
import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.messaging.FirebaseMessaging
import androidx.core.net.toUri

class MainActivity : AppCompatActivity() {

    private val REQUEST_NOTIFICATION_PERMISSION = 100 // permission code(can be any other number)
    private var permissionHandled = false // flag to check whether permission was given or not

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        requestNotificationPermissionAndStartService()

        findViewById<Button>(R.id.accessibility_permission_button).setOnClickListener {
            if (checkAccessibilityPermission(ScreenScanningService::class.java)) {
                Toast.makeText(
                    this,
                    R.string.already_have_permission,
                    Toast.LENGTH_LONG
                ).show()
            } else {
                askForAccessibilityPermission()
            }
        }

        findViewById<Button>(R.id.overlay_permission_button).setOnClickListener {
            if (checkOverlayPermission()) {
                Toast.makeText(
                    this,
                    R.string.already_have_permission,
                    Toast.LENGTH_LONG
                ).show()
            } else {
                askForOverlayPermission()
            }
        }
    }

    private fun requestNotificationPermissionAndStartService() {
        // checks Android Version(Android 13+)
        // checks whether a permission is already granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            // requests permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_NOTIFICATION_PERMISSION
            )
        }
        else {
            // the permission is already granted or Android Version is lower than 13(permission is not needed)
            startPostService()
            permissionHandled = true
        }
    }

    // actions after user's answer
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // checks the request and runs the service
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION && !permissionHandled) {
            startPostService()
            permissionHandled = true
        }
    }

    // starts service in the foreground
    private fun startPostService() {
        val serviceIntent = Intent(this, ForegroundDispatchService::class.java)
        ContextCompat.startForegroundService(applicationContext, serviceIntent)
    }

    // checks if the user has already given permission for ScreenScanningService
    private fun checkAccessibilityPermission(service : Class<out AccessibilityService>): Boolean {
        val expectedComponentName = ComponentName(this, service)
        val enabledServices = Settings.Secure.getString(
            this.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)

        if (enabledServices == null) {
            return false
        }
        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServices)

        for (service in colonSplitter) {
            val componentName = ComponentName.
            unflattenFromString(service)
            if (componentName == expectedComponentName) {
                return true
            }
        }

        return false
    }

    // redirects user to the settings to give a permission for ScreenScanningService
    private fun askForAccessibilityPermission() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun checkOverlayPermission() : Boolean {
        return Settings.canDrawOverlays(this)
    }

    private fun askForOverlayPermission() {
        val permissionIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            "package:$packageName".toUri())
        permissionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(permissionIntent)
    }
}