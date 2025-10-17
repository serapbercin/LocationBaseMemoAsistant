package com.sap.codelab.ui

import android.content.Intent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.core.LocationPermissions.missingForGeofencing
import com.example.featurememo.MemoFeature
import com.example.featurememo.MemoRoutes
import com.example.featurememo.di.LocalMemoRepository
import com.example.featurememo.di.LocalViewModelFactory
import com.example.featurememo.ui.memoNavGraph
import com.example.notification.MemoLocation
import com.sap.codelab.App
import com.sap.codelab.MainActivity
import mu.KotlinLogging

@Composable
internal fun MemoApp(activityIntent: Intent?) {
    val log = KotlinLogging.logger {}
    val nav = rememberNavController()

    val memoId by rememberUpdatedState(
        newValue = activityIntent?.getLongExtra(MainActivity.EXTRA_MEMO_ID, -1L) ?: -1L
    )

    val app = LocalContext.current.applicationContext as App
    val vmFactory = remember { app.appComponent.viewModelFactory() }
    val repo = remember { app.appComponent.memoRepository() }
    val geofenceManager = remember { app.appComponent.geofenceManager() }
    val ctx = LocalContext.current

    CompositionLocalProvider(
        LocalMemoRepository provides repo,
        LocalViewModelFactory provides vmFactory
    ) {
        LaunchedEffect(memoId) {
            if (memoId > 0) {
                log.debug { "NAV: Opening editor for memoId=$memoId (from notification)" }
                nav.navigate(MemoRoutes.editor(memoId)) {
                    popUpTo(MemoFeature.startRoute) { inclusive = false }
                    launchSingleTop = true
                }
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
                        val missing = missingForGeofencing(ctx)
                        if (missing.isNotEmpty()) {
                            log.warn { "Missing permissions for geofence: $missing" }
                            return@memoNavGraph
                        }
                        try {
                            geofenceManager.addGeofenceForMemo(
                                memo = MemoLocation(
                                    id = savedId,
                                    lat = latLng.latitude,
                                    lon = latLng.longitude
                                )
                            )
                        } catch (se: SecurityException) {
                            log.warn { "Geofence add failed: ${se.message}" }
                        }
                    }
                )
            }
        }
    }
}
