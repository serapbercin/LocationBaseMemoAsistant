package com.sap.codelab.di

import com.example.notification.MemoContentIntentFactory
import com.example.notification.MemoDetailsProvider
import com.example.notification.MemoProvider
import com.sap.codelab.AppMemoDetailsProvider
import com.sap.codelab.AppMemoIntentFactory
import com.sap.codelab.AppMemoProvider
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class NotificationsBindings {
    @Binds @Singleton abstract fun bindMemoProvider(impl: AppMemoProvider): MemoProvider
    @Binds @Singleton abstract fun bindMemoDetailsProvider(impl: AppMemoDetailsProvider): MemoDetailsProvider
    @Binds @Singleton abstract fun bindMemoContentIntentFactory(impl: AppMemoIntentFactory): MemoContentIntentFactory
}