package com.example.familyguardapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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
}