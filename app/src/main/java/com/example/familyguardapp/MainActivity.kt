package com.example.familyguardapp

import android.Manifest
import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

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

        findViewById<Button>(R.id.permission_button).setOnClickListener {
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

        val spinner = findViewById<Spinner>(R.id.spinner)

        val languages : MutableMap<String, Int> = mutableMapOf() // map for languages
        languages["en"] = 0
        languages["ru"] = 1

        val selectedLanguageCode = getSavedLanguage(this)
        val position = languages[selectedLanguageCode] ?: 0
        spinner.setSelection(position) // sets new language

        var isFirstSelection = true // to avoid repeated screen updates

        // listener
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (isFirstSelection) {
                    isFirstSelection = false
                    return
                }
                val selectedLanguage = parent?.getItemAtPosition(position).toString() // gets selected item
                val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
                val currentLanguage = getSavedLanguage(this@MainActivity) // previous(current) language

                val newLanguage = when(selectedLanguage) {
                    "Russian" -> "ru"
                    "Английский" -> "en"
                    else -> currentLanguage
                }

                if (currentLanguage != newLanguage) {
                    prefs.edit { putString("app_language", newLanguage) }
                    updateScreen()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("Nothing", "none")
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

    // changes context for activity, in this example we change locale
    override fun attachBaseContext(newBase: Context?) {
        val contextWithLocale = updateLocale(newBase!!, getSavedLanguage(newBase))
        super.attachBaseContext(contextWithLocale)
    }

    // returns languageCode of last chosen language
    private fun getSavedLanguage(context : Context) : String {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return prefs.getString("app_language", "en") ?: "en"
    }

    // updates locale, returns new context for activity
    private fun updateLocale(context : Context, languageCode : String) : Context {
        val locale = Locale(languageCode) // creates new locale
        Locale.setDefault(locale) // sets new locale for app
        val config = context.resources.configuration // takes current configuration
        config.setLocale(locale) // applies new configuration(locale)
        return context.createConfigurationContext(config) // returns new context
    }

    // updates activity
    private fun updateScreen() {
        recreate()
    }
}