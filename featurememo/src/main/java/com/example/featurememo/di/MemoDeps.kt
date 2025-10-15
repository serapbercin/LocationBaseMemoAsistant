package com.example.featurememo.di

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModelProvider
import com.example.domain.MemoRepositoryApi

val LocalMemoRepository = staticCompositionLocalOf<MemoRepositoryApi> {
    error("IMemoRepository is not provided")
}

val LocalViewModelFactory = staticCompositionLocalOf<ViewModelProvider.Factory> {
    ViewModelProvider.NewInstanceFactory()
}