package com.sap.codelab

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.data.Repository
import com.example.featurememo.LocalMemoRepository
import com.example.featurememo.MemoFeature
import com.example.featurememo.MemoRoutes
import com.example.featurememo.memoNavGraph

class MainActivity : ComponentActivity() {

    private val reqNotif =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
    private val reqFine =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted && Build.VERSION.SDK_INT >= 29) reqBg.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    private val reqBg = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    private var latestIntent by mutableStateOf<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                reqNotif.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                Log.i("PERMISSIONS", "POST_NOTIFICATIONS already granted.")
            }
        }
        reqFine.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        latestIntent = intent
        setContent { MemoApp(latestIntent) }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        latestIntent = intent
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

@RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
@Composable
private fun MemoApp(activityIntent: Intent?) {
    val nav = rememberNavController()

    val incomingMemoId = remember(activityIntent) {
        activityIntent?.getLongExtra(MainActivity.EXTRA_MEMO_ID, -1L) ?: -1L
    }
    var handled by rememberSaveable { mutableStateOf(false) }

    val appCtx = LocalContext.current.applicationContext
    val geofenceManager = remember { GeofenceManager(appCtx) }

    CompositionLocalProvider(
        LocalMemoRepository provides Repository
    ) {
        LaunchedEffect(incomingMemoId) {
            if (!handled && incomingMemoId > 0) {
                nav.navigate(MemoRoutes.edit(incomingMemoId)) {
                    popUpTo(MemoFeature.startRoute) { inclusive = false }
                    launchSingleTop = true
                }
                handled = true
            }
        }

        MaterialTheme {
            NavHost(
                navController = nav,
                startDestination = MemoFeature.startRoute
            ) {
                memoNavGraph(
                    nav = nav,
                    onRegisterGeofence = { savedId, latLng ->
                        try {
                            geofenceManager.addGeofenceForMemo(
                                memoId = savedId,
                                lat = latLng.latitude,
                                lng = latLng.longitude,
                                radiusMeters = 500f
                            )
                        } catch (se: SecurityException) {
                            Log.w("MemoApp", "Geofence add failed: ${se.message}")
                        }
                    }
                )
            }
        }
    }
}