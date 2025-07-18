package com.example.familyguardapp.screens

import android.Manifest
import android.accessibilityservice.AccessibilityService
import android.app.AlarmManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.familyguardapp.R
import com.example.familyguardapp.receivers.DailyReportReceiver
import com.example.familyguardapp.receivers.MyDeviceAdminReceiver
import com.example.familyguardapp.services.ContentBlockingService
import com.example.familyguardapp.services.ForegroundDispatchService
import com.example.familyguardapp.services.ScreenScanningService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val REQUEST_NOTIFICATION_PERMISSION = 100 // request code for displaying notification
    private val REQUEST_DEVICE_ADMIN = 101
    private var permissionHandled = false // flag to check whether permission is already given

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var adminComponent: ComponentName
    private lateinit var passwordField: EditText // password field
    private lateinit var disableAdminButton: Button // button, which disables device admin

    private val accessibilityPermissionCheckScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val overlayPermissionCheckScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // this standard method creates activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        scheduleDailyReport()

        val alarmBtn = findViewById<Button>(R.id.alarm_permission_button)

        if (hasAlarmPermission()) {
            alarmBtn.visibility = View.INVISIBLE
        }

        alarmBtn.setOnClickListener {
            if (!hasAlarmPermission()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
                startAlarmPermissionLoop(alarmBtn)
            }
        }

        devicePolicyManager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponent = ComponentName(this, MyDeviceAdminReceiver::class.java)

        val backButton = findViewById<FloatingActionButton>(R.id.backButtonMain) // back button

        backButton.imageTintList = ColorStateList.valueOf(Color.BLACK) // makes arrow black

        // sets the listener for back button
        backButton.setOnClickListener {
            val intent = Intent(this@MainActivity, LoginActivity::class.java) // intent to launch LoginActivity
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP // flags are needed to create activity if it does not exist, otherwise launch the existing instance of activity
            startActivity(intent) // starts activity
            finish() // finishes LoginActivity
            backButton.isEnabled = false // disables back button
        }

        passwordField = findViewById(R.id.password_field) // initializes passwordField
        disableAdminButton = findViewById(R.id.disable_admin_button) // initializes disableAdminButton

        requestNotificationPermissionAndStartService() // asks user for permission to show notifications
        requestDeviceAdminPermission()

        if (!isPasswordSet()) {
            showSetPasswordDialog()
        }

        val accessibilityBtn = findViewById<Button>(R.id.accessibility_permission_button)
        if (checkAccessibilityPermission(
                mutableListOf(
                        ScreenScanningService::class.java,
                        ContentBlockingService::class.java
                    )
            )
        ) {
            accessibilityBtn.visibility = View.INVISIBLE
        }
        accessibilityBtn.setOnClickListener {
            if (!checkAccessibilityPermission(
                    mutableListOf(
                            ScreenScanningService::class.java,
                            ContentBlockingService::class.java
                        )
                )
            ) {
                askForAccessibilityPermission()
                startAccessibilityPermissionCheckLoop(accessibilityBtn)
            }
        }

        val spinner = findViewById<Spinner>(R.id.spinner) // spinner is used to change app language

        val languages: MutableMap<String, Int> = mutableMapOf() // map for languages
        languages["en"] = 0 // English language has position 0
        languages["ru"] = 1 // Russian language has position 1

        val selectedLanguageCode = getSavedLanguage(this) // get the last language, which was chosen by user
        val position = languages[selectedLanguageCode] ?: 0 // gets the position of the last language or gets 0(English by default)
        spinner.setSelection(position) // sets new language

        var isFirstSelection = true // to avoid repeated screen updates

        // sets the listener for spinner
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // this method performs actions when an item is selected
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (isFirstSelection) { // to avoid repeated changes of language
                    isFirstSelection = false
                    return
                }
                val selectedLanguage = parent?.getItemAtPosition(position).toString() // gets selected item
                val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE) // gets SharedPreferences
                val currentLanguage = getSavedLanguage(this@MainActivity) // previous(current) language

                val newLanguage = when (selectedLanguage) { // checks all possible variants
                    "Russian" -> "ru"
                    "Английский" -> "en"
                    else -> currentLanguage
                }

                if (currentLanguage != newLanguage) {
                    prefs.edit { putString("app_language", newLanguage) } // saves choice for next sessions
                    updateScreen() // updates the screen
                }
            }

            // this method performs actions when nothing is selected
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("Nothing", "none") // checks logs
            }
        }

        val overlayBtn = findViewById<Button>(R.id.overlay_permission_button)
        if (checkOverlayPermission()) {
            overlayBtn.visibility = View.INVISIBLE
        }
        overlayBtn.setOnClickListener {
            if (!checkOverlayPermission()) {
                askForOverlayPermission()
                startOverlayPermissionCheckLoop(overlayBtn)
            }
        }

        // sets listener to disable admin button
        disableAdminButton.setOnClickListener {
            val enteredPassword = passwordField.text.toString() // extracts password from password field
            if (verifyPassword(enteredPassword)) { // verifies correctness of the password
                disableDeviceAdmin() // disables Device Admin
            } else {
                Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show() // notifies user about wrong password
                passwordField.text.clear() // clears password field
            }
        }
    }

    private fun showSetPasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_set_password, null)
        val passwordInput = dialogView.findViewById<EditText>(R.id.password_input)
        val passwordConfirm = dialogView.findViewById<EditText>(R.id.password_confirm)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancel_button)
        val confirmButton = dialogView.findViewById<Button>(R.id.confirm_button)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        cancelButton.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        confirmButton.setOnClickListener {
            val password = passwordInput.text.toString()
            val confirmPassword = passwordConfirm.text.toString()

            when {
                password.isEmpty() -> {
                    Toast.makeText(this, "Enter the password for device admin", Toast.LENGTH_SHORT).show()
                }
                password.length < 6 -> {
                    Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
                }
                password != confirmPassword -> {
                    Toast.makeText(this, "The passwords do not match", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    savePassword(password)
                    dialog.dismiss()
                    Toast.makeText(this, "Password set", Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialog.show()
    }

    private fun savePassword(password: String) {
        val passwordHash = hashPassword(password)
        val prefs = getSharedPreferences("admin_settings", MODE_PRIVATE)
        prefs.edit()
            .putString("password_hash", passwordHash)
            .putBoolean("password_set", true)
            .apply()
    }
    private fun hasAlarmPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.canScheduleExactAlarms()
        } else {
            true
        }
    }

    private fun startAlarmPermissionLoop(btn: Button) {
        CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                if (hasAlarmPermission()) {
                    btn.visibility = View.INVISIBLE
                    return@launch
                }
                delay(2000)
            }
        }
    }

    private fun isPasswordSet(): Boolean {
        val prefs = getSharedPreferences("admin_settings", MODE_PRIVATE)
        return prefs.getBoolean("password_set", false)
    }

    private fun verifyPassword(password: String): Boolean {
        if (password.isEmpty()) return false

        val prefs = getSharedPreferences("admin_settings", MODE_PRIVATE)
        val savedHash = prefs.getString("password_hash", "")
        val enteredHash = hashPassword(password)

        return savedHash == enteredHash
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    private fun disableDeviceAdmin() {
        if (devicePolicyManager.isAdminActive(adminComponent)) {
            try {
                val prefs = getSharedPreferences("admin_settings", MODE_PRIVATE)
                prefs.edit().putBoolean("allow_admin_disable", true).apply()

                devicePolicyManager.removeActiveAdmin(adminComponent)
                Toast.makeText(this, "Device Admin disabled", Toast.LENGTH_SHORT).show()
                passwordField.text.clear()
            } catch (e: Exception) {
                Toast.makeText(this, "Error disabling Device Admin", Toast.LENGTH_SHORT).show()
                Log.e("MainActivity", "Error disabling device admin", e)
            }
        } else {
            Toast.makeText(this, "Device Admin is already disabled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestDeviceAdminPermission() {
        if (!devicePolicyManager.isAdminActive(adminComponent)) {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
            intent.putExtra(
                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Required for parental control app protection"
            )
            startActivityForResult(intent, REQUEST_DEVICE_ADMIN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_DEVICE_ADMIN -> {
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "Administrator rights have been obtained", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Administrator rights not granted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun requestNotificationPermissionAndStartService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_NOTIFICATION_PERMISSION
            )
        } else {
            startPostService()
            permissionHandled = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_NOTIFICATION_PERMISSION && !permissionHandled) {
            startPostService()
            permissionHandled = true
        }
    }

    private fun startPostService() {
        val serviceIntent = Intent(this, ForegroundDispatchService::class.java)
        ContextCompat.startForegroundService(applicationContext, serviceIntent)
    }

    private fun checkAccessibilityPermission(services: List<Class<out AccessibilityService>>): Boolean {
        val expectedComponentNames = mutableListOf<ComponentName>()
        for (i in services.indices) {
            val expectedComponentName = ComponentName(this, services[i])
            expectedComponentNames.add(expectedComponentName)
        }

        val enabledServices = Settings.Secure.getString(
            this.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )

        if (enabledServices == null) {
            return false
        }

        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServices)

        for (service in colonSplitter) {
            val componentName = ComponentName
                .unflattenFromString(service)
            if (expectedComponentNames.contains(componentName)) {
                expectedComponentNames.remove(componentName)
            }
        }

        return expectedComponentNames.isEmpty()
    }

    private fun startAccessibilityPermissionCheckLoop(accessibilityBtn: Button) {
        accessibilityPermissionCheckScope.launch {
            while (isActive) { // Runs until coroutine is cancelled or permission is granted
                if (checkAccessibilityPermission(
                        mutableListOf(
                                ScreenScanningService::class.java,
                                ContentBlockingService::class.java
                            )
                    )
                ) {
                    // Permission granted
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainActivity,
                            R.string.permission_granted,
                            Toast.LENGTH_SHORT
                        ).show()

                        accessibilityBtn.visibility = View.INVISIBLE
                    }
                    return@launch // Exit coroutine
                }

                // Check every 2 seconds
                delay(2000)
            }
        }
    }

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
    private fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return prefs.getString("app_language", "en") ?: "en"
    }

    // updates locale, returns new context for activity
    private fun updateLocale(context: Context, languageCode: String): Context {
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
    private fun checkOverlayPermission(): Boolean {
        return Settings.canDrawOverlays(this)
    }

    private fun askForOverlayPermission() {
        val permissionIntent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            "package:$packageName".toUri()
        )
        permissionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(permissionIntent)
    }

    private fun scheduleDailyReport() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        val intent = Intent(this, DailyReportReceiver::class.java)
        val pendingIntent = android.app.PendingIntent.getBroadcast(
            this,
            0,
            intent,
            android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT
        )

        val calendar = java.util.Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(java.util.Calendar.HOUR_OF_DAY, 23)
            set(java.util.Calendar.MINUTE, 59)
            set(java.util.Calendar.SECOND, 0)
        }

        // Если текущее время уже позже 23:59, перенести на завтра
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }

        alarmManager.setRepeating(
            android.app.AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            android.app.AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

        Log.d("DailyReport", "Alarm set for 23:59 every day")
    }

    private fun startOverlayPermissionCheckLoop(overlayBtn: Button) {
        overlayPermissionCheckScope.launch {
            while (isActive) { // Runs until coroutine is cancelled or permission is granted
                if (checkOverlayPermission()) {
                    // Permission granted
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainActivity,
                            R.string.permission_granted,
                            Toast.LENGTH_SHORT
                        ).show()

                        overlayBtn.visibility = View.INVISIBLE
                    }
                    return@launch // Exit coroutine
                }

                // Check every 2 seconds
                delay(2000)
            }
        }
    }
}
