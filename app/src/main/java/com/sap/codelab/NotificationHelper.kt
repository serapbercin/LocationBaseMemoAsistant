package com.sap.codelab


import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import com.sap.codelab.model.Memo

class NotificationHelper(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "memo_reminders"
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID, "Memo Reminders", NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showMemoNotification(memo: Memo) {
        val text = memo.description.take(140)
        val n = NotificationCompat.Builder(context, CHANNEL_ID)
            // .setSmallIcon(R.drawable.ic_location_reminder) // add such a drawable
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(memo.title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(memo.id.toInt(), n)
    }
}
