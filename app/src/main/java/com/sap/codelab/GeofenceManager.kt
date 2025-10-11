package com.sap.codelab


import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.*

class GeofenceManager(private val context: Context) {

    private val client = LocationServices.getGeofencingClient(context)

    private val pendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java).setAction(ACTION)
        PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun addGeofence(memoId: Long, lat: Double, lng: Double, radiusMeters: Float = 200f) {
        val geofence = Geofence.Builder()
            .setRequestId(memoId.toString())
            .setCircularRegion(lat, lng, radiusMeters)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        client.addGeofences(request, pendingIntent)
    }

    fun removeGeofence(memoId: Long) {
        client.removeGeofences(listOf(memoId.toString()))
    }

    companion object { const val ACTION = "com.sap.codelab.ACTION_GEOFENCE_EVENT" }
}
