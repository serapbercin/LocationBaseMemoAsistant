package com.sap.codelab.di

import android.content.Context
import com.example.notification.GeofenceManager
import com.example.notification.MemoProvider
import com.example.notification.NotificationHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object NotificationsModule {
    @Provides @Singleton
    fun provideNotificationHelper(context: Context): NotificationHelper =
        NotificationHelper(context)

    @Provides @Singleton
    fun provideGeofenceManager(
        context: Context,
        memoProvider: MemoProvider
    ): GeofenceManager = GeofenceManager(context, memoProvider)
}