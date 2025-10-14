package com.sap.codelab

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.data.Repository

/**
 * Extension of the Android Application class.
 */
internal class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize the repository
        Repository.initialize(this)



        // Create the notification channel for memo reminders
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "memo_reminders",            // must match NotificationHelper
                "Memo Reminders",            // user-visible name
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Shows notifications when you reach memo locations."
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}