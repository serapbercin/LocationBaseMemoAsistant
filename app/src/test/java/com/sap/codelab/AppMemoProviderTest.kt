package com.sap.codelab

import com.example.domain.Memo
import com.example.domain.MemoRepositoryApi
import com.example.notification.MemoLocation
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AppMemoProviderTest {

    private val repo: MemoRepositoryApi = mockk()
    private val sut = AppMemoProvider(repo)
    private val now = 1_730_000_000_000L

    @Test
    fun `given memos with various locations when getAllMemosWithLocation called then only valid ones are returned`() = runTest {
        // Given
        val validLat = 52_520000L   // microdegrees
        val validLon = 13_405000L   // microdegrees
        val memos = listOf(
            Memo(1L, "Valid", "", now, validLat, validLon, isDone = false),   // keep
            Memo(null, "Null ID", "", now, validLat, validLon, isDone = false),// drop
            Memo(2L, "Zero Lat", "", now, 0L, validLon, isDone = false),       // drop
            Memo(3L, "Zero Lon", "", now, validLat, 0L, isDone = true)         // drop
        )
        coEvery { repo.getAll() } returns memos

        // When
        val result: List<MemoLocation> = sut.getAllMemosWithLocation()

        // Then
        assertEquals(1, result.size)
        val only = result.first()
        assertEquals(1L, only.id)
        assertEquals(validLat / MICRODEGREES_TO_DEGREES, only.lat, 1e-6)
        assertEquals(validLon / MICRODEGREES_TO_DEGREES, only.lon, 1e-6)
    }
}
