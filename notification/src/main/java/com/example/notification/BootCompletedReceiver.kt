package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootCompletedReceiver : BroadcastReceiver() {
    @androidx.annotation.RequiresPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        Log.d("BootCompleted", "Re-adding geofences after boot")

        val provider = NotificationsConfig.memoProvider ?: return  // app must set this
        val mgr = GeofenceManager(context.applicationContext, provider)

        // Repository.initialize(context) uygulama açılışında yapıldıysa, burada direkt kullanabiliriz.
        CoroutineScope(Dispatchers.IO).launch {
            mgr.reAddAllGeofences()
        }
    }
}