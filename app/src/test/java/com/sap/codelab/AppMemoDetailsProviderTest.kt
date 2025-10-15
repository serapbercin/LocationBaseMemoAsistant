package com.sap.codelab

import com.example.core.config.MemoConfig
import com.example.domain.Memo
import com.example.domain.usecases.GetMemoByIdUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AppMemoDetailsProviderTest {

    private val getMemoByIdUseCase: GetMemoByIdUseCase = mockk()
    private val sut = AppMemoDetailsProvider(getMemoByIdUseCase)
    private val now = 1_730_000_000_000L

    @Test
    fun `given memo with long description when getDetails called then preview is truncated`() =
        runTest {
            // Given
            val longDescription = "x".repeat(MemoConfig.PREVIEW_MAX_LENGTH + 5)
            coEvery { getMemoByIdUseCase(42L) } returns Memo(
                id = 42L,
                title = "My Memo",
                description = longDescription,
                reminderDate = now,
                reminderLatitude = 0L,
                reminderLongitude = 0L,
                isDone = false
            )

            // When
            val result = sut.getDetails(42L)

            // Then
            requireNotNull(result)
            assertEquals("My Memo", result.title)
            assertEquals(longDescription.take(MemoConfig.PREVIEW_MAX_LENGTH), result.preview)
        }

    @Test
    fun `given memo with short description when getDetails called then preview is unchanged`() =
        runTest {
            // Given
            val shortDescription = "short text"
            coEvery { getMemoByIdUseCase(1L) } returns Memo(
                id = 1L,
                title = "Short",
                description = shortDescription,
                reminderDate = now,
                reminderLatitude = 0L,
                reminderLongitude = 0L,
                isDone = false
            )

            // When
            val result = sut.getDetails(1L)

            // Then
            requireNotNull(result)
            assertEquals("Short", result.title)
            assertEquals(shortDescription, result.preview)
        }

    @Test
    fun `given memo with exact preview length when getDetails called then preview remains full length`() =
        runTest {
            // Given
            val exactDescription = "y".repeat(MemoConfig.PREVIEW_MAX_LENGTH)
            coEvery { getMemoByIdUseCase(9L) } returns Memo(
                id = 9L,
                title = "Boundary",
                description = exactDescription,
                reminderDate = now,
                reminderLatitude = 0L,
                reminderLongitude = 0L,
                isDone = true
            )

            // When
            val result = sut.getDetails(9L)

            // Then
            requireNotNull(result)
            assertEquals("Boundary", result.title)
            assertEquals(exactDescription, result.preview)
        }
}
