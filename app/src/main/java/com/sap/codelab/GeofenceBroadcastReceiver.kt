package com.sap.codelab

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.data.Repository
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Receives geofence transition events and shows a notification
 * when the user enters a memo's defined location.
 */

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) {
            Log.e("GeoReceiver", "Intent null")
            return
        }
        val event = GeofencingEvent.fromIntent(intent)
        if (event == null) {
            Log.e("GeoReceiver", "GeofencingEvent null")
            return
        }
        if (event.hasError()) {
            Log.e("GeoReceiver", "error=${event.errorCode}")
            return
        }

        val transition = when (event.geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> "ENTER"
            Geofence.GEOFENCE_TRANSITION_EXIT  -> "EXIT"
            Geofence.GEOFENCE_TRANSITION_DWELL -> "DWELL"
            else -> "UNKNOWN"
        }

        val requestIds = event.triggeringGeofences?.mapNotNull { it.requestId } ?: emptyList()
        if (requestIds.isEmpty()) return

        val helper = NotificationHelper(context)

        // Repository zaten var; burada onu kullan.
        // Repository.initialize(...) çağrısının app açılışında yapılmış olması gerekir.
        CoroutineScope(Dispatchers.IO).launch {
            for (rid in requestIds) {
                val memoId = rid.toLongOrNull() ?: continue
                runCatching { Repository.getMemoById(memoId) }
                    .onSuccess { memo ->
                        val title = memo.title
                        val preview = memo.description.take(140)
                        Log.d("GeoReceiver", "Transition=$transition id=${memo.id} title=$title")
                        helper.showNotificationForMemo(
                            memoId = memo.id!!,
                            title = title,
                            text = preview,
                            iconRes = android.R.drawable.ic_menu_mylocation // istediğin bir ikonla değiştir
                        )
                    }
                    .onFailure { e ->
                        Log.e("GeoReceiver", "getMemoById($memoId) failed", e)
                    }
            }
        }
    }
}