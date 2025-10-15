package com.sap.codelab

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.notification.NotificationsConfig
import com.sap.codelab.di.DaggerAppComponent

/**
 * Extension of the Android Application class.
 */
internal class App : Application() {

    lateinit var appComponent: com.sap.codelab.di.AppComponent
        private set

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.factory().create(this)

        // wire notifications
        NotificationsConfig.intentFactory   = appComponent.memoIntentFactory()
        NotificationsConfig.memoProvider    = appComponent.memoProvider()
        NotificationsConfig.detailsProvider = appComponent.memoDetailsProvider()

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