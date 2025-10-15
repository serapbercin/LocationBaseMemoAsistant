package com.example.data.di

import androidx.room.Room
import android.content.Context
import com.example.data.AppDatabase
import com.example.data.MemoDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DataProvidersModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        )
            .build()

    @Provides
    fun provideMemoDao(db: AppDatabase): MemoDao = db.memoDao()
}