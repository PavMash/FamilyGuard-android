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

var fcm_token: String? = null

class AutoStartFirebaseMessagingService : FirebaseMessagingService() {
    private val channelId by lazy { getString(R.string.default_notification_channel_id) } // channel ID

    private val NOTIFICATION_ID = 7 // notification ID

    // refreshes the token
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        fcm_token = token
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel() // creates a channel for notifications
    }

    // when a message is received
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val notification = createNotification() // notification
        val notificationManager = getSystemService(NotificationManager::class.java) // manager
        notificationManager.notify(NOTIFICATION_ID, notification) // displays the notification

        Log.d("FCM", "Got a message: ${message.from}")

        message.notification?.let {
            Log.d("FCM", "Title: ${it.title}")
            Log.d("FCM", "Body: ${it.body}")
        }

        message.data.let {
            Log.d("FCM", "Data: $it")

            // if we have "autostart" we run our ForegroundDispatchService
            // Accessibility service also launches
            if (it["action"] == "autostart") {
                val serviceIntent = Intent(this, ForegroundDispatchService::class.java)
                ContextCompat.startForegroundService(applicationContext, serviceIntent)
            }
        }
    }

    private fun createNotificationChannel() {
        // checks Android Version(Android O or higher versions require notification channel)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "MyNotificationChannel",
                NotificationManager.IMPORTANCE_NONE
            ) // IMPORTANCE_NONE is needed to hide the notification
            channel.enableVibration(false) // no vibration when the notification comes
            channel.setSound(null, null) // no sound as well
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel) // creates the channel
        }
    }

    // creates notification
    private fun createNotification(): Notification {
        return NotificationCompat
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
