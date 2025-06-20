package com.example.familyguardapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import androidx.core.content.edit

// starts CloudDispatchService in the background with a notification
class ForegroundDispatchService : Service() {
    // channel id
    private val channelId = "ForegroundDispatchService"

    // coroutine
    private var serviceJob : Job? = null

    // Retrofit instance
    private val retrofit by lazy {
        Retrofit
            .Builder()
            .baseUrl("http://10.91.53.178:8000/") // base url without endpoint
            .addConverterFactory(MoshiConverterFactory.create()) // converter for JSON
            .build()
    }

    // CloudDispatchService instance
    private val cloudDispatchService by lazy {
        retrofit.create(CloudDispatchService::class.java)
    }

    // creates a channel
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    // service does not need to interact with activity
    override fun onBind(intent: Intent?): IBinder? = null

    // service actions
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification() // notification to display
        startForeground(1, notification) // start service in the foreground
        serviceJob = CoroutineScope(Dispatchers.IO).launch { // coroutine
            delay(30_000L)

            while (isActive) { // while coroutine is running
                try {
                    val prefs = getSharedPreferences("scanner_data", MODE_PRIVATE)
                    val capturedText = prefs.getString("captured_text", "") // captured text
                    val sourceApp = prefs.getString("source_app", "")
                    if (capturedText != null && capturedText != "" && !sourceApp.isNullOrEmpty()
                        && sourceApp != packageName) {
                        Log.d("ForegroundService", "Captured text: $capturedText")
                        postText(capturedText) // function for testing
                    }
                    // postImage() // function for testing
                }
                catch (e : Exception) {
                    Log.d("MyLog", "${e.message}")
                }
                delay(10000) // 10 seconds delay
            }
        }
        return START_STICKY // app will try to restart service if it is stopped
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob?.cancel() // stops coroutine to avoid memory leaks
    }

    // creates notification channel
    private fun createNotificationChannel() {
        // checks Android Version(Android O or higher versions require notification channel)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "MyNotificationChannel",
                NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    // creates notification
    private fun createNotification() : Notification {
        return NotificationCompat
            .Builder(this, channelId)
            .setContentTitle("FamilyGuardApp Service") // title
            .setContentText("Your device can be monitored") // content
            .setSmallIcon(R.drawable.ic_launcher_foreground) // icon
            .setOngoing(true) // permanent notification
            .build()
    }

    private suspend fun postText(capturedText : String) {
        // example of text data
        blockTheApp()
        val request: RequestBody = capturedText.toRequestBody("text/plain".toMediaType())
        // response from the Cloud
        val response = cloudDispatchService.sendData("228337", request)
        // checks success of response
        if (response.isSuccessful) {
            Log.d("MyLog", "Success(text)")
            //blockTheApp()
            getSharedPreferences("scanner_data", MODE_PRIVATE)
                .edit {
                    remove("captured_text")
                    remove("source_app")
                }
        }
        else {
            Log.d("MyLog", "${response.errorBody()?.string()}")
            Log.d("MyLog", "Fail(text)")
        }
    }

    private suspend fun postImage() {
        // example of image data
        val request = createImagePartFromDrawable(this, R.drawable.android)
        // response from server
        val response = cloudDispatchService.sendImage(request)
        // checks success of response
        if (response.isSuccessful) {
            Log.d("MyLog", "Success(image)")
        }
        else {
            Log.d("MyLog", "${response.errorBody()?.string()}")
            Log.d("MyLog", "Fail(image)")
        }
    }

    // converts drawable into Part
    private fun createImagePartFromDrawable(context: Context, drawableId: Int): MultipartBody.Part {
        val inputStream = context.resources.openRawResource(drawableId) // drawable file
        val bytes = inputStream.readBytes()
        inputStream.close()

        val file = File(context.cacheDir, "upload_image.png") // temporary file
        file.writeBytes(bytes)

        val mediaType = "image/png".toMediaType() // type
        val requestBody = file.asRequestBody(mediaType)

        return MultipartBody.Part.createFormData("image", file.name, requestBody) // Part
    }

    private fun blockTheApp() {
        Log.d("Lox", "LoxLox")
        val blockIntent = Intent(this, BlockingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(blockIntent)
    }
}