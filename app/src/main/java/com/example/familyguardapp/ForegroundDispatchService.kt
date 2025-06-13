package com.example.familyguardapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Base64
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.familyguardapp.data.ContentItem
import com.example.familyguardapp.data.ImageData
import com.example.familyguardapp.data.ImageMessage
import com.example.familyguardapp.data.ImageSource
import com.example.familyguardapp.data.Message
import com.example.familyguardapp.data.ScreenData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

// starts CloudDispatchService in the background with a notification
class ForegroundDispatchService : Service() {
    // channel id
    private val channelId = "ForegroundDispatchService"

    // coroutine
    private var serviceJob : Job? = null

    private val API_KEY = BuildConfig.API_KEY

    // Retrofit instance
    private val retrofit by lazy {
        Retrofit
            .Builder()
            .baseUrl("https://api.proxyapi.ru/anthropic/v1/") // base url without endpoint
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
            while (isActive) { // while coroutine is running
                try {
                    val prefs = getSharedPreferences("scanner_data", MODE_PRIVATE)
                    val capturedText = prefs.getString("captured_text", "") // captured text
                    Log.d("ForegroundService", "Captured text: $capturedText")
                    if (capturedText != null && capturedText != "") {
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
                NotificationManager.IMPORTANCE_DEFAULT)
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
        val request = ScreenData(messages = listOf(Message(role = "user", content = capturedText)))
        // response from server
        val response = cloudDispatchService.sendData(
            authHeader = "Bearer $API_KEY",
            request = request)
        // checks success of response
        if (response.isSuccessful) {
            Log.d("MyLog", "Success(text)")
        }
        else {
            Log.d("MyLog", "${response.errorBody()?.string()}")
            Log.d("MyLog", "Fail(text)")
        }
    }

    private suspend fun postImage() {
        val base64Image =
            encodeDrawableToBase64(this, R.drawable.android)
        // example of image data
        val request = ImageData(
            messages = listOf(
                ImageMessage(
                    content = listOf(
                        ContentItem(
                            type = "text",
                            text = "FamilyGuardApp"
                        ),
                        ContentItem(
                            type = "image",
                            source = ImageSource(
                                data = base64Image
                            )
                        )
                    )
                )
            )
        )
        // response from server
        val response = cloudDispatchService.sendImage(
            authHeader = "Bearer $API_KEY",
            request = request)
        // checks success of response
        if (response.isSuccessful) {
            Log.d("MyLog", "Success(image)")
        }
        else {
            Log.d("MyLog", "${response.errorBody()?.string()}")
            Log.d("MyLog", "Fail(image)")
        }
    }

    // converts images into base64Image strings
    fun encodeDrawableToBase64(context: Context, drawableId: Int): String {
        val inputStream = context.resources.openRawResource(drawableId)
        val bytes = inputStream.readBytes()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
}