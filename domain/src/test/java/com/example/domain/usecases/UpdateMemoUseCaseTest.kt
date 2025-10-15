package com.example.domain.usecases

import com.example.domain.Memo
import com.example.domain.MemoRepositoryApi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class UpdateMemoUseCaseTest {

    private val repo: MemoRepositoryApi = mockk(relaxed = true)
    private val useCase = UpdateMemoUseCase(repo)

    @Test
    fun `returns true when rows updated, shaping fields correctly`() = runTest {
        // Arrange
        val cap = slot<Memo>()
        coEvery { repo.updateMemo(capture(cap)) } returns 1

        val updated = useCase(
            id = 5L,
            title = "  Edited  ",
            description = "  Body ",
            lat = 40.0,
            lng = -3.7,
            whenMillis = 999L
        )

        assertTrue(updated)
        coVerify(exactly = 1) { repo.updateMemo(any()) }

        val actual = cap.captured
        // Assert
        assertEquals(5L, actual.id)
        assertEquals("Edited", actual.title)
        assertEquals("Body", actual.description)
        assertEquals(999L, actual.reminderDate)
        assertEquals((40.0 * 1e7).toLong(), actual.reminderLatitude)
        assertEquals((-3.7 * 1e7).toLong(), actual.reminderLongitude)
        assertFalse(actual.isDone)
    }

    @Test
    fun `returns false when repo updates 0 rows`() = runTest {
        // Arrange
        coEvery { repo.updateMemo(any()) } returns 0

        // Act
        val updated = useCase(1L, "t", "d", 0.0, 0.0)

        // Assert
        assertFalse(updated)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws when id is not positive`() = runTest {
        // Act
        useCase(0L, "t", "d", 0.0, 0.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws when title is blank`() = runTest {
        // Act
        useCase(7L, "   ", "d", 0.0, 0.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws when description is blank`() = runTest {
        // Act
        useCase(7L, "t", "   ", 0.0, 0.0)
    }
}
