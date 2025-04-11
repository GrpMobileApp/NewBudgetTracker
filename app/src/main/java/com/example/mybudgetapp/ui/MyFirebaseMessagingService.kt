package com.example.mybudgetapp.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.app.Notification
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // This function handles the notification sending when called
    fun sendOverspendNotification(context: Context, subCategoryName: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "overspend_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Overspending Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Create notification
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Subcategory Exceeded!")
            .setContentText("Your subcategory '$subCategoryName' has exceeded its remaining budget!")
            .setSmallIcon(R.drawable.logomain)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(subCategoryName.hashCode(), notification)
    }
}