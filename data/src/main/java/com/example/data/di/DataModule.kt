package com.example.data.di

import com.example.data.MemoRepository
import com.example.domain.MemoRepositoryApi
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

internal const val DATABASE_NAME = "codelab"

@Module
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindMemoRepository(memoRepository: MemoRepository): MemoRepositoryApi
}