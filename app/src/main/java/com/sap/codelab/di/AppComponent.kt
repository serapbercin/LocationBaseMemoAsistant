package com.sap.codelab.di

import android.app.Application
import com.example.data.di.DataModule
import com.example.data.di.DataProvidersModule
import com.example.domain.MemoRepositoryApi
import com.example.notification.GeofenceManager
import com.example.notification.MemoContentIntentFactory
import com.example.notification.MemoDetailsProvider
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        NotificationsBindings::class,
        NotificationsModule::class,
        DataProvidersModule::class,
        DataModule::class
    ]
)
interface AppComponent {

    // Expose what you need in app startup / UI
    fun geofenceManager(): GeofenceManager
    fun memoProvider(): com.example.notification.MemoProvider
    fun memoDetailsProvider(): MemoDetailsProvider
    fun memoIntentFactory(): MemoContentIntentFactory
    fun memoRepository(): MemoRepositoryApi

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance app: Application): AppComponent
    }
}