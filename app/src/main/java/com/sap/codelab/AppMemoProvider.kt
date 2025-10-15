package com.sap.codelab

import com.example.notification.MemoLocation
import com.example.notification.MemoProvider
import com.example.data.Repository  // your existing data source
import com.example.notification.MemoDetails
import com.example.notification.MemoDetailsProvider

class AppMemoProvider : MemoProvider {
    override suspend fun getAllMemosWithLocation(): List<MemoLocation> {
        val scale = 1_000_000.0 // match your storage scale
        return Repository.getAll().mapNotNull { m ->
            val lat = m.reminderLatitude
            val lon = m.reminderLongitude
            if (lat != 0L && lon != 0L)
                MemoLocation(m.id!!, lat / scale, lon / scale)
            else null
        }
    }
}