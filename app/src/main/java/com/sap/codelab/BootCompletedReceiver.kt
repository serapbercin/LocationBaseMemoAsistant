package com.sap.codelab

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sap.codelab.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        // Re-register geofences for open memos
        CoroutineScope(Dispatchers.IO).launch {
            val mgr = GeofenceManager(context)
            Repository.getOpen().forEach { memo ->
                val lat = memo.reminderLatitude / 1e7
                val lng = memo.reminderLongitude / 1e7
                mgr.addGeofence(memo.id, lat, lng, 200f)
            }
        }
    }
}