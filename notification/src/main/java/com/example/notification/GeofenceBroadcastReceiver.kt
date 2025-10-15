package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    @androidx.annotation.RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent?) {
        val event = intent?.let { GeofencingEvent.fromIntent(it) } ?: run {
            Log.e("GeoReceiver", "Null event")
            return
        }
        if (event.hasError()) {
            Log.e("GeoReceiver", "error=${event.errorCode}")
            return
        }

        val transition = when (event.geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> "ENTER"
            Geofence.GEOFENCE_TRANSITION_EXIT -> "EXIT"
            Geofence.GEOFENCE_TRANSITION_DWELL -> "DWELL"
            else -> "UNKNOWN"
        }

        val ids =
            event.triggeringGeofences?.mapNotNull { it.requestId.toLongOrNull() } ?: emptyList()
        if (ids.isEmpty()) return

        val helper = NotificationHelper(context)
        val detailsProvider = NotificationsConfig.detailsProvider
        if (NotificationsConfig.intentFactory == null) {
            Log.w("GeoReceiver", "intentFactory not set; skipping")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            for (memoId in ids) {
                val details = detailsProvider?.runCatching { getDetails(memoId) }?.getOrNull()
                val title = details?.title ?: "Nearby memo"
                val preview = details?.preview ?: "You’re close to a saved location. Tap to open."

                Log.d("GeoReceiver", "Transition=$transition id=$memoId title=$title")

                helper.showNotificationForMemo(
                    memoId = memoId,
                    title = title,
                    text = preview,
                    iconRes = R.drawable.ic_launcher_background
                )
            }
        }
    }
}
