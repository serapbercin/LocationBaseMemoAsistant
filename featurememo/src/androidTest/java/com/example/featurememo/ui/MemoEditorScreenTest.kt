package com.example.featurememo.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.domain.Memo
import com.example.domain.usecases.GetMemoByIdUseCase
import com.example.domain.usecases.SaveMemoUseCase
import com.example.domain.usecases.UpdateMemoUseCase
import com.example.featurememo.R
import com.example.featurememo.di.LocalViewModelFactory
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class MemoEditorScreenTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private fun factory(
        get: GetMemoByIdUseCase,
        save: SaveMemoUseCase,
        update: UpdateMemoUseCase
    ) = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val ctor = modelClass.constructors.first()
            @Suppress("UNCHECKED_CAST")
            return ctor.newInstance(get, save, update) as T
        }
    }

    @Test
    fun edit_prefills_fields() {
        val get: GetMemoByIdUseCase = mockk()
        val save: SaveMemoUseCase = mockk()
        val update: UpdateMemoUseCase = mockk()
        coEvery { get(7L) } returns Memo(
            id = 7L,
            title = "Old",
            description = "Body",
            reminderDate = 0L,
            reminderLatitude = (1.0 * 1e7).toLong(),
            reminderLongitude = (2.0 * 1e7).toLong(),
            isDone = false
        )
        coEvery { update(any(), any(), any(), any(), any(), any()) } returns true

        rule.setContent {
            val nav = rememberNavController()
            androidx.compose.runtime.CompositionLocalProvider(
                LocalViewModelFactory provides factory(get, save, update)
            ) {
                MemoEditorScreen(nav = nav, memoId = 7L)
            }
        }
        rule.onNodeWithText("Old").assertExists()
        rule.onNodeWithText("Body").assertExists()
        rule.onNodeWithText(rule.activity.getString(R.string.cta_save), useUnmergedTree = true)
            .assertExists()
    }
}
