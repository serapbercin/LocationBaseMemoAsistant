package com.sap.codelab

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat


class NotificationHelper(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "memo_geo"
        private const val CHANNEL_NAME = "Memo Geofence"
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            )
        }
    }

    /**
     * Bildirim: başlık + ilk 140 karakter, tıklamada MainActivity(edit/<memoId>) açar.
     */
    fun showNotificationForMemo(
        memoId: Long,
        title: String,
        text: String,
        iconRes: Int
    ) {
        ensureChannel()

        // 🔑 ÖNEMLİ: PendingIntent’i hep buradan, doğru extra ile üret
        val openIntent = MainActivity.intent(context, memoId)

        val flags =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            else
                PendingIntent.FLAG_UPDATE_CURRENT

        val contentPi = PendingIntent.getActivity(context, 0, openIntent, flags)

        val notif = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(iconRes)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(contentPi)
            .setAutoCancel(true)
            .build()

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(System.currentTimeMillis().toInt(), notif)
    }
}

