package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.core.LocationPermissions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import mu.KotlinLogging

class BootCompletedReceiver : BroadcastReceiver() {

    private val log = KotlinLogging.logger {}

    @androidx.annotation.RequiresPermission(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        val pending = goAsync()
        val appCtx = context.applicationContext

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                if (!LocationPermissions.hasFine(appCtx) ||
                    !LocationPermissions.hasBackground(appCtx)
                ) {
                    log.warn { "BootCompleted: Missing location permissions; skip geofence restore." }
                    return@launch
                }

                val provider = NotificationsConfig.memoProvider ?: return@launch
                GeofenceManager(appCtx, provider).reAddAllGeofences()
                log.info { "Geofences restored after boot." }
            } catch (t: Throwable) {
                log.error(t) { "Error restoring geofences" }
            } finally {
                pending.finish()
            }
        }
    }
}