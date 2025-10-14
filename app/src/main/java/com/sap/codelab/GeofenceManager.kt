package com.sap.codelab

import android.Manifest
import androidx.annotation.RequiresPermission
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.data.Repository
import com.example.domain.Memo
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class GeofenceManager(private val context: Context) {

    private val client: GeofencingClient = LocationServices.getGeofencingClient(context)

    // Long → Double dönüşümü için ölçek. (Senin kaydetme biçimine göre 1e6 veya 1e7 olabilir.)
    private val COORD_SCALE = 1_000_000.0

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun addGeofenceForMemo(memo: Memo, onDone: (Boolean, Throwable?) -> Unit = { _, _ -> }) {
        val requestId = memo.id.toString()

        val lat = memo.reminderLatitude / COORD_SCALE
        val lon = memo.reminderLongitude / COORD_SCALE

        val geofence = Geofence.Builder()
            .setRequestId(requestId)
            .setCircularRegion(
                /* latitude  */ lat,
                /* longitude */ lon,
                /* radius    */ 200f           // gereksinim: 200 m
            )
            .setTransitionTypes(
                Geofence.GEOFENCE_TRANSITION_ENTER or
                        Geofence.GEOFENCE_TRANSITION_EXIT
            )
            .setNotificationResponsiveness(2000) // daha hızlı teslim (testte çok işe yarar)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val pi = PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, GeofenceBroadcastReceiver::class.java),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            else
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        client.addGeofences(request, pi)
            .addOnSuccessListener { onDone(true, null) }
            .addOnFailureListener { e ->
                Log.e("GeofenceManager", "addGeofences failed", e)
                onDone(false, e)
            }
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun addGeofenceForMemo(
        memoId: Long,
        lat: Double,
        lng: Double,
        radiusMeters: Float = 200f,
        onDone: (Boolean, Throwable?) -> Unit = { _, _ -> }
    ) {
        val requestId = memoId.toString()

        val geofence = Geofence.Builder()
            .setRequestId(requestId)
            .setCircularRegion(
                /* latitude  */ lat,
                /* longitude */ lng,
                /* radius    */ radiusMeters   // çağrıda 200f/500f ne verirsen
            )
            .setTransitionTypes(
                Geofence.GEOFENCE_TRANSITION_ENTER or
                        Geofence.GEOFENCE_TRANSITION_EXIT
            )
            .setNotificationResponsiveness(2000) // hızlı teslim
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val pi = PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, GeofenceBroadcastReceiver::class.java),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            else
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        client.addGeofences(request, pi)
            .addOnSuccessListener { onDone(true, null) }
            .addOnFailureListener { e ->
                Log.e("GeofenceManager", "addGeofences failed", e)
                onDone(false, e)
            }
    }


    @androidx.annotation.RequiresPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
    /** Boot sonrası tüm geofenceleri Repository üzerinden yeniden ekle */
    fun reAddAllGeofencesFromRepository() {
        try {
            // Repository.getAll() WorkerThread; bu çağrı UI'da yapılmasın.
            val all = Repository.getAll()
            all.forEach { addGeofenceForMemo(it) }
        } catch (t: Throwable) {
            Log.e("GeofenceManager", "reAddAllGeofencesFromRepository failed", t)
        }
    }
}
