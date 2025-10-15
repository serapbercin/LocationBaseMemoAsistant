// notifications module
package com.example.notification

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.*

class GeofenceManager(
    private val context: Context,
    private val memoProvider: MemoProvider
) {
    private val client = LocationServices.getGeofencingClient(context)

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun addGeofenceForMemo(
        memo: MemoLocation,
        radiusMeters: Float = 200f,
        onDone: (Boolean, Throwable?) -> Unit = { _, _ -> }
    ) {
        val geofence = Geofence.Builder()
            .setRequestId(memo.id.toString())
            .setCircularRegion(memo.lat, memo.lon, radiusMeters)
            .setTransitionTypes(
                Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT
            )
            .setNotificationResponsiveness(2000)
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
            .addOnSuccessListener { onDone(true, null) }
            .addOnFailureListener { e -> Log.e("GeofenceManager", "addGeofences failed", e); onDone(false, e) }
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    suspend fun reAddAllGeofences() {
        memoProvider.getAllMemosWithLocation().forEach { addGeofenceForMemo(it) }
    }
}
