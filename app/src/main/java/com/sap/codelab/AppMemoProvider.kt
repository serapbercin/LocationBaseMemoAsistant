package com.sap.codelab

import com.example.notification.MemoLocation
import com.example.notification.MemoProvider
import com.example.domain.MemoRepositoryApi
import javax.inject.Inject

class AppMemoProvider @Inject constructor(private val repo: MemoRepositoryApi) : MemoProvider {
    override suspend fun getAllMemosWithLocation(): List<MemoLocation> {
        val scale = 1_000_000.0
        return repo.getAll().mapNotNull { m ->
            val lat = m.reminderLatitude
            val lon = m.reminderLongitude
            if (lat != 0L && lon != 0L) MemoLocation(m.id!!, lat / scale, lon / scale) else null
        }
    }
}