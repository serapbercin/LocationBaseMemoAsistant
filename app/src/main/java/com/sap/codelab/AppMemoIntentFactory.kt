package com.sap.codelab

import android.app.PendingIntent
import android.content.Context
import android.os.Build
import com.example.notification.MemoContentIntentFactory

class AppMemoIntentFactory : MemoContentIntentFactory {
    override fun forMemo(context: Context, memoId: Long): PendingIntent {
        val intent = MainActivity.intent(context, memoId)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        else PendingIntent.FLAG_UPDATE_CURRENT
        return PendingIntent.getActivity(context, memoId.hashCode(), intent, flags)
    }
}
