package com.sap.codelab

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.sap.codelab.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Receives geofence transition events and shows a notification
 * when the user enters a memo's defined location.
 */
class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Ensure the correct action
        if (intent.action != GeofenceManager.ACTION) return

        val event = GeofencingEvent.fromIntent(intent) ?: return
        if (event.hasError()) return
        if (event.geofenceTransition != Geofence.GEOFENCE_TRANSITION_ENTER) return

        // 🔒 Runtime permission check
        val finePerm = Manifest.permission.ACCESS_FINE_LOCATION
        val bgPerm = Manifest.permission.ACCESS_BACKGROUND_LOCATION

        val hasFine = ContextCompat.checkSelfPermission(context, finePerm) ==
                PackageManager.PERMISSION_GRANTED
        val hasBg = ContextCompat.checkSelfPermission(context, bgPerm) ==
                PackageManager.PERMISSION_GRANTED

        if (!hasFine && !hasBg) {
            // No location permission; skip processing
            return
        }

        // Get memo ID from geofence request ID
        val id = event.triggeringGeofences
            ?.firstOrNull()
            ?.requestId
            ?.toLongOrNull()
            ?: return

        // Keep the receiver alive during async work
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Fetch memo from DB (suspend)
                val memo = Repository.getMemoById(id)

                // Show notification (main-safe)
                withContext(Dispatchers.Main) {
                    NotificationHelper(context).showMemoNotification(memo)
                }

                // Optionally: mark as done or remove geofence
                // Repository.saveMemo(memo.copy(isDone = true))
                // GeofenceManager(context).removeGeofence(id)
            } finally {
                // Signal the system that we're finished
                pendingResult.finish()
            }
        }
    }
}
