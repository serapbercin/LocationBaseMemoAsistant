package com.example.notification

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresPermission
import com.example.core.config.GeofenceConfig
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import mu.KotlinLogging

//TODO consder Dagger setup for NotificationHelper
class GeofenceManager(
    private val context: Context,
    private val memoProvider: MemoProvider
) {
    private val client = LocationServices.getGeofencingClient(context)
    private val log = KotlinLogging.logger {}

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun addGeofenceForMemo(
        memo: MemoLocation,
        radiusMeters: Float = GeofenceConfig.DEFAULT_RADIUS_METERS,
        onDone: (Boolean, Throwable?) -> Unit = { _, _ -> }
    ) {
        val geofence = Geofence.Builder()
            .setRequestId(memo.id.toString())
            .setCircularRegion(memo.lat, memo.lon, radiusMeters)
            .setTransitionTypes(
                Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val flags =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            else PendingIntent.FLAG_UPDATE_CURRENT

        val pi = PendingIntent.getBroadcast(
            context,
            memo.id.hashCode(),
            Intent(context, GeofenceBroadcastReceiver::class.java),
            flags
        )

        client.addGeofences(request, pi)
            .addOnSuccessListener {
                log.info { "GF: add success id=${memo.id} (${memo.lat},${memo.lon})" }; onDone(
                true,
                null
            )
            }
            .addOnFailureListener { e -> log.error(e) { "GF: add failed" }; onDone(false, e) }

    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    suspend fun reAddAllGeofences() {
        memoProvider.getAllMemosWithLocation().forEach { addGeofenceForMemo(it) }
    }
}
