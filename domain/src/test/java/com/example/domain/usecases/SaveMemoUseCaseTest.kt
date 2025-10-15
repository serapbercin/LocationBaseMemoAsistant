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

class SaveMemoUseCaseTest {

    private val repo: MemoRepositoryApi = mockk(relaxed = true)
    private val useCase = SaveMemoUseCase(repo)

    @Test
    fun `saves memo with trimmed strings and E7 scaling`() = runTest {
        // Arrange
        val captured = slot<Memo>()
        coEvery { repo.saveMemoAndReturnId(capture(captured)) } returns 123L

        val id = useCase(
            title = "  Title  ",
            description = "  Desc ",
            lat = 52.520008,
            lng = 13.404954,
            whenMillis = 111L
        )

        assertEquals(123L, id)
        coVerify(exactly = 1) { repo.saveMemoAndReturnId(any()) }

        val actual = captured.captured

        // Assert
        assertEquals(0L, actual.id)
        assertEquals("Title", actual.title)
        assertEquals("Desc", actual.description)
        assertEquals(111L, actual.reminderDate)
        assertEquals((52.520008 * 1e7).toLong(), actual.reminderLatitude)
        assertEquals((13.404954 * 1e7).toLong(), actual.reminderLongitude)
        assertFalse(actual.isDone)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws when title is blank`() = runTest {
        // Act
        useCase("   ", "ok", 0.0, 0.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws when description is blank`() = runTest {
        // Act
        useCase("ok", "   ", 0.0, 0.0)
    }
}
