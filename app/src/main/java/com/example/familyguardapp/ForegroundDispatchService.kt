package com.example.familyguardapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.edit
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

// starts CloudDispatchService in the background with a hidden notification
class ForegroundDispatchService : Service() {
    // channel id for hidden notification
    private val channelId = "HiddenChannel"

    // coroutine
    private var serviceJob: Job? = null

    private lateinit var networkService: NetworkService // network service

    private lateinit var okHttpClient: OkHttpClient // HTTP client, which avoids problems with Android protection

    // Retrofit instance
    private val retrofit by lazy {
        Retrofit
            .Builder()
            .baseUrl("https://5.129.222.88/") // base url without endpoint
            .client(okHttpClient) // to bypass Android protection
            .addConverterFactory(MoshiConverterFactory.create()) // converter for JSON
            .build()
    }

    // CloudDispatchService instance
    private val cloudDispatchService by lazy {
        retrofit.create(CloudDispatchService::class.java)
    }

    // this standard method creates the service and initializes HTTP client
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        networkService = NetworkService(this)
        okHttpClient = networkService.createOkHttpClient()
    }

    // service does not need to interact with activity
    override fun onBind(intent: Intent?): IBinder? = null

    // this method defines service actions
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification() // notification to display (hidden)
        startForeground(1, notification) // start service in the foreground
        serviceJob = CoroutineScope(Dispatchers.IO).launch { // coroutine
//            delay(30_000L)

            while (isActive) { // while coroutine is running
                try {
                    val prefs = getSharedPreferences("scanner_data", MODE_PRIVATE) // gets SharedPreferences storage
                    val capturedText = prefs.getString("captured_text", "") // captured text
                    val sourceApp = prefs.getString("source_app", "") // gets source app
                    if (capturedText != null && capturedText != "" && !sourceApp.isNullOrEmpty() &&
                        sourceApp != packageName // checks whether the source app is not our app
                    ) {
                        Log.d("ForegroundService", "Captured text: $capturedText") // checks logs
                        postText(capturedText) // function for testing
                    }
                } catch (e: Exception) {
                    Log.d("MyLog", "${e.message}") // checks the description of caught exception
                }
                delay(10000) // 10 seconds delay
            }
        }
        return START_STICKY // app will try to restart service if it is stopped
    }

    // this method is called when the app is destroyed
    override fun onDestroy() {
        super.onDestroy()
        serviceJob?.cancel() // stops coroutine to avoid memory leaks
    }

    // creates notification channel(it is must be done from Android Oreo API 26(8.0) to see notification)
    // Foreground Services must have notifications
    private fun createNotificationChannel() {
        // checks Android Version(Android O or higher versions require notification channel)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, // channel ID
                "Background Service", // name of the channel
                NotificationManager.IMPORTANCE_MIN // IMPORTANCE_MIN does not disturb a child
            ).apply {
                description = "Service channel" // description for user
                enableLights(false) // disables lights
                enableVibration(false) // disables vibration
                setSound(null, null) // disables sound
                setShowBadge(false) // disables badge
                lockscreenVisibility = Notification.VISIBILITY_SECRET // not visible in lockscreen
            }

            val manager = getSystemService(NotificationManager::class.java) // manages notifications
            manager.createNotificationChannel(channel) // shows notification to user
        }
    }

    // creates a hidden notification
    private fun createNotification(): Notification {
        return NotificationCompat
            .Builder(this, channelId)
            .setContentTitle(getString(R.string.dispatch_notification_title)) // title
            .setContentText(getString(R.string.dispatch_notification_text)) // content
            .setSmallIcon(R.drawable.shield) // icon
            .setOngoing(true) // permanent notification
            .build()
    }

    // this method sends captured data to the server
    private suspend fun postText(capturedText: String) {
        val bytes = capturedText.toByteArray(Charsets.UTF_8) // converts text to bytes
        val request: RequestBody = bytes.toRequestBody("text/plain".toMediaType()) // creates request
        val textPart = MultipartBody.Part.createFormData("file", "data.txt", request) // transforms to file
        val response = cloudDispatchService.sendData(childID!!, textPart) // response from the server
        // checks success of response
        if (response.isSuccessful) {
            Log.d("MyLog", "Success(text)") // checks logs
            val responseBody = response.body() // gets response body
            if (responseBody != null) {
                val responseBlock = responseBody.block // gets information about blocking
                // checks contents of "yes/no" block of response body
                if (responseBlock == "Yes") { // if blocking is confirmed
                    blockTheApp() // blocks the content
                }
            }
            getSharedPreferences("scanner_data", MODE_PRIVATE) // gets SharedPreferences
                .edit {
                    remove("captured_text") // clears captured_text
                    remove("source_app") // clears the name of the source app
                }
        } else {
            Log.d("MyLog", "${response.errorBody()?.string()}") // checks error message
            Log.d("MyLog", "Fail(text)") // checks failure
        }
    }

    // this method blocks the app
    // starts BlockingActivity
    private fun blockTheApp() {
        Log.d("ForegroundService", "Blocking started") // checks logs
        val blockingIntent = Intent(ContentBlockingService.OVERLAY_TRIGGER_ACTION)
        LocalBroadcastManager.getInstance(this).sendBroadcast(
            blockingIntent
        ) // overlays the current app with the screen of warning
    }
}
