package com.sap.codelab

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.notification.NotificationHelper.Companion.CHANNEL_ID
import com.example.notification.NotificationsConfig
import com.sap.codelab.di.DaggerAppComponent

internal class App : Application() {

    lateinit var appComponent: com.sap.codelab.di.AppComponent
        private set

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.factory().create(this)

        NotificationsConfig.intentFactory = appComponent.memoIntentFactory()
        NotificationsConfig.memoProvider = appComponent.memoProvider()
        NotificationsConfig.detailsProvider = appComponent.memoDetailsProvider()

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = ContextCompat.getSystemService(this, NotificationManager::class.java)
            ?: return

        val memoChannel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.channel_memo_reminders_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = getString(R.string.channel_memo_reminders_desc)
            setShowBadge(true)
        }

        manager.createNotificationChannel(memoChannel)
    }
}