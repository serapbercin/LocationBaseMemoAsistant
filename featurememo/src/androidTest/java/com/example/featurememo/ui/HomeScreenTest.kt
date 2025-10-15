package com.example.featurememo.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.domain.Memo
import com.example.featurememo.HomeUiState
import com.example.featurememo.R
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun loading_showsProgress() {
        val state = HomeUiState(isLoading = true)
        rule.setContent {
            HomeScreen(
                state = state,
                snackbarHostState = androidx.compose.material3.SnackbarHostState(),
                onAdd = {},
                onOpen = {},
                onDelete = {}
            )
        }
        rule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertExists()
    }

    @Test
    fun empty_showsEmptyText() {
        val state = HomeUiState(isLoading = false, memos = emptyList())
        rule.setContent {
            HomeScreen(
                state = state,
                snackbarHostState = androidx.compose.material3.SnackbarHostState(),
                onAdd = {},
                onOpen = {},
                onDelete = {}
            )
        }
        rule.onNodeWithText(rule.activity.getString(R.string.empty_memos)).assertExists()
    }

    @Test
    fun list_showsItems_and_handlesClicks() {
        val memos = listOf(
            Memo(1L, "Title A", "Desc A", 0L, 0L, 0L, false),
            Memo(2L, "Title B", "Desc B", 0L, 0L, 0L, false)
        )
        val state = HomeUiState(isLoading = false, memos = memos)
        var addClicked = false
        var openedId: Long? = null
        var deletedId: Long? = null

        rule.setContent {
            HomeScreen(
                state = state,
                snackbarHostState = androidx.compose.material3.SnackbarHostState(),
                onAdd = { addClicked = true },
                onOpen = { openedId = it },
                onDelete = { deletedId = it }
            )
        }

        rule.onNodeWithText("Title A").assertExists()
        rule.onNodeWithText("Title B").assertExists()

        rule.onNodeWithContentDescription(rule.activity.getString(R.string.cta_add)).performClick()
        assert(addClicked)

        rule.onNodeWithText("Title A").performClick()
        assert(openedId == 1L)

        rule.onAllNodesWithContentDescription(rule.activity.getString(R.string.cta_delete))[0].performClick()
        assert(deletedId == 1L)
    }
}
