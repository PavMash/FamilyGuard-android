package com.example.familyguardapp

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class MyDeviceAdminReceiver : DeviceAdminReceiver() {

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Toast.makeText(context, "Device administrator activated", Toast.LENGTH_SHORT).show()
    }

    override fun onDisableRequested(context: Context, intent: Intent): CharSequence {
        val prefs = context.getSharedPreferences("admin_settings", Context.MODE_PRIVATE)
        val allowDisable = prefs.getBoolean("allow_admin_disable", false)

        return if (allowDisable) {
            prefs.edit().putBoolean("allow_admin_disable", false).apply()
            "Disabling device administrator..."
        } else {
            val blockIntent = Intent(context, BlockingActivity::class.java)
            blockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(blockIntent)

            "To disable the administrator, use the password in the application"
        }
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Toast.makeText(context, "Device administrator is disabled", Toast.LENGTH_SHORT).show()
    }
}