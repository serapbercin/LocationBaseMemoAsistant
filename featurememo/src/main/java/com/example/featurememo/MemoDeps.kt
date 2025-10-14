package com.example.featurememo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.domain.IMemoRepository
import com.example.featurememo.ui.EditMemoScreen

// App katmanı bunu provide edecek
val LocalMemoRepository = staticCompositionLocalOf<IMemoRepository> {
    error("IMemoRepository is not provided")
}

// Basit VM factory (Dagger yokken iş görür)
fun <VM : ViewModel> simpleFactory(create: () -> VM) = object : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = create() as T
}


fun createMemoVMFactory(repo: IMemoRepository) = object : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CreateMemoViewModel2(repo) as T
    }
}

fun editMemoVMFactory(repo: IMemoRepository) = object : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EditMemoViewModel(repo) as T
    }
}