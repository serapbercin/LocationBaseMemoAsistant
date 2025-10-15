package com.example.featurememo.ui


import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.domain.Memo
import com.example.featurememo.R
import org.junit.Rule
import org.junit.Test

class MemoRowTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun shows_and_clicks() {
        val memo = Memo(10L, "Hello", "World", 0L, 0L, 0L, false)
        var open = false
        var delete = false
        rule.setContent {
            MemoRow(memo = memo, onOpen = { open = true }, onDelete = { delete = true })
        }
        rule.onNodeWithText("Hello").assertExists().performClick()
        assert(open)
        rule.onAllNodesWithContentDescription(rule.activity.getString(R.string.cta_delete))[0].performClick()
        assert(delete)
    }
}
