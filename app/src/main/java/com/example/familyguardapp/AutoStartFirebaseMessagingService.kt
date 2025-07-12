package com.example.familyguardapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

var fcm_token: String? = null // device FCM token

// this class is used to autostart the app using FCM
class AutoStartFirebaseMessagingService : FirebaseMessagingService() {
    private val channelId by lazy { getString(R.string.default_notification_channel_id) } // channel ID

    private val NOTIFICATION_ID = 7 // notification ID

    // this method refreshes the token
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        fcm_token = token // gets new token
    }

    // this standard method creates the service
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel() // creates a channel for notifications
    }

    // this method handles received messages
    // "message" comes from FCM
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val notification = createNotification() // notification
        val notificationManager = getSystemService(NotificationManager::class.java) // manager
        notificationManager.notify(NOTIFICATION_ID, notification) // displays the notification

        Log.d("FCM", "Got a message: ${message.from}") // checks where the message came from

        message.notification?.let { // checks logs
            Log.d("FCM", "Title: ${it.title}") // checks the title of the message
            Log.d("FCM", "Body: ${it.body}") // checks the body of the message
        }

        message.data.let {
            Log.d("FCM", "Data: $it") // checks the data inside the message

            // if we have "autostart" we run our ForegroundDispatchService
            // Accessibility service also launches
            if (it["action"] == "autostart") {
                Log.d("FCM", "Starting app...")
                val serviceIntent = Intent(this, ForegroundDispatchService::class.java) // intent
                ContextCompat.startForegroundService(applicationContext, serviceIntent) // launches ForegroundDispatchService
            }
        }
    }

    // creates notification channel(it is must be done from Android Oreo API 26(8.0) to see notification)
    // Foreground Services must have notifications
    private fun createNotificationChannel() {
        // checks Android Version(Android Oreo or higher versions require notification channel)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, // channel ID
                "MyNotificationChannel", // name of the channel
                NotificationManager.IMPORTANCE_NONE // IMPORTANCE_NONE is needed to hide the notification
            )
            channel.enableVibration(false) // no vibration when the notification comes
            channel.setSound(null, null) // no sound as well
            val manager = getSystemService(NotificationManager::class.java) // manages notifications
            manager.createNotificationChannel(channel) // creates the channel
        }
    }

    // this method creates notification
    private fun createNotification(): Notification {
        return NotificationCompat // builds notification
            .Builder(this, channelId)
            .setContentTitle(getString(R.string.firebase_notification_title)) // title
            .setContentText(getString(R.string.firebase_notification_text)) // content
            .setSmallIcon(R.drawable.android) // icon
            .setVibrate(longArrayOf(0)) // no vibration
            .setSound(null) // no sound
            .setPriority(1) // HIGH priority
            .build()
    }
}