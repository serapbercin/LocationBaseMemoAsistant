package com.sap.codelab

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.core.LocationPermissions
import com.example.core.NotificationPermissions.canPost
import com.example.core.NotificationPermissions.requiredPermissionOrNull
import com.sap.codelab.ui.MemoApp
import mu.KotlinLogging

class MainActivity : ComponentActivity() {

    private val log = KotlinLogging.logger {}
    private lateinit var reqMultiple: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        reqMultiple = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { results -> log.debug { "PERMISSIONS: requested: $results" } }

        val initialMissing = buildList {
            requiredPermissionOrNull()?.let { post ->
                if (!canPost(this@MainActivity)) add(post)
            }
            addAll(LocationPermissions.missingForForegroundLocation(this@MainActivity))
        }
        if (initialMissing.isNotEmpty()) {
            reqMultiple.launch(initialMissing.toTypedArray())
        }

        val firstIntent = intent
        setContent { MemoApp(firstIntent) }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    companion object {
        const val EXTRA_MEMO_ID = "extra_memo_id"
        fun intent(context: Context, memoId: Long) =
            Intent(context, MainActivity::class.java).apply {
                putExtra(EXTRA_MEMO_ID, memoId)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
    }
}