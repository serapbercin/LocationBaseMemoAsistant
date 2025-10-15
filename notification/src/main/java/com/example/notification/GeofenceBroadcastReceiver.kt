package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.core.NotificationPermissions
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mu.KotlinLogging

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    private val log = KotlinLogging.logger {}

    @androidx.annotation.RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent?) {
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val event = intent?.let { GeofencingEvent.fromIntent(it) }
                if (event == null) {
                    log.warn { "GeoReceiver: Null GeofencingEvent" }
                    return@launch
                }
                if (event.hasError()) {
                    log.error { "GeoReceiver: Geofencing error=${event.errorCode}" }
                    return@launch
                }

                if (!NotificationPermissions.canPost(context)) {
                    log.warn { "POST_NOTIFICATIONS not granted; skipping notify" }
                    return@launch
                }

                val transition = when (event.geofenceTransition) {
                    Geofence.GEOFENCE_TRANSITION_ENTER -> "ENTER"
                    Geofence.GEOFENCE_TRANSITION_EXIT -> "EXIT"
                    Geofence.GEOFENCE_TRANSITION_DWELL -> "DWELL"
                    else -> "UNKNOWN"
                }

                val ids = event.triggeringGeofences
                    ?.mapNotNull { it.requestId.toLongOrNull() }
                    .orEmpty()
                if (ids.isEmpty()) return@launch

                val helper = NotificationHelper(context)
                val detailsProvider = NotificationsConfig.detailsProvider
                val intentFactory = NotificationsConfig.intentFactory
                if (intentFactory == null || detailsProvider == null) {
                    log.warn { "GeoReceiver: NotificationsConfig not wired; skipping notify" }
                    return@launch
                }

                for (memoId in ids) {
                    val details = runCatching { detailsProvider.getDetails(memoId) }.getOrNull()
                    val title = details?.title ?: "Nearby memo"
                    val preview =
                        details?.preview ?: "You’re close to a saved location. Tap to open."

                    log.warn { "GeoReceiver: Transition=$transition id=$memoId title=$title" }

                    helper.showNotificationForMemo(
                        memoId = memoId,
                        title = title,
                        text = preview,
                        iconRes = R.drawable.ic_notification_small
                    )
                }
            } finally {
                pending.finish()
            }
        }
    }
}
