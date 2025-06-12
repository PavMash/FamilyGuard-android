package com.example.familyguardapp

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<Button>(R.id.permission_button).setOnClickListener {
            if(checkAccessibilityPermission()) {
                Toast.makeText(this, R.string.already_have_permission,
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    fun checkAccessibilityPermission () : Boolean {
        var accessEnabled = 0
        try {
            accessEnabled = Settings.Secure.getInt(
                this.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED)
        } catch (e : Settings.SettingNotFoundException) {
            e.printStackTrace()
        }
        if (accessEnabled == 0) {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            return false
        } else {
            return true
        }
    }
}