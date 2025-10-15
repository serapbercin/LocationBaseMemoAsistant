package com.example.notification

import android.app.PendingIntent
import android.content.Context

data class MemoLocation(val id: Long, val lat: Double, val lon: Double)

interface MemoProvider {
    suspend fun getAllMemosWithLocation(): List<MemoLocation>
}

data class MemoDetails(val title: String, val preview: String)

interface MemoDetailsProvider {
    suspend fun getDetails(memoId: Long): MemoDetails?
}

interface MemoContentIntentFactory {
    fun forMemo(context: Context, memoId: Long): PendingIntent
}

/** App fills these at startup (Application.onCreate) */
object NotificationsConfig {
    @Volatile var memoProvider: MemoProvider? = null
    @Volatile var detailsProvider: MemoDetailsProvider? = null
    @Volatile var intentFactory: MemoContentIntentFactory? = null
}
