package com.sap.codelab

import com.example.domain.MemoRepositoryApi
import com.example.notification.MemoLocation
import com.example.notification.MemoProvider
import javax.inject.Inject

internal const val MICRODEGREES_TO_DEGREES = 1_000_000.0

class AppMemoProvider @Inject constructor(private val repo: MemoRepositoryApi) : MemoProvider {
    override suspend fun getAllMemosWithLocation(): List<MemoLocation> {
        return repo.getAll().mapNotNull { memo ->
            val id = memo.id ?: return@mapNotNull null
            val lat = memo.reminderLatitude
            val lon = memo.reminderLongitude

            if (lat != 0L && lon != 0L) {
                MemoLocation(id, lat / MICRODEGREES_TO_DEGREES, lon / MICRODEGREES_TO_DEGREES)
            } else {
                null
            }
        }
    }
}