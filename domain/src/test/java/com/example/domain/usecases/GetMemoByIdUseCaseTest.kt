package com.example.domain.usecases

import com.example.domain.Memo
import com.example.domain.MemoRepositoryApi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetMemoByIdUseCaseTest {

    private val repo: MemoRepositoryApi = mockk()
    private val useCase = GetMemoByIdUseCase(repo)

    @Test
    fun `returns memo from repository`() = runTest {
        // Given
        val expected = Memo(
            id = 10L,
            title = "T",
            description = "D",
            reminderDate = 1L,
            reminderLatitude = 2L,
            reminderLongitude = 3L,
            isDone = false
        )
        coEvery { repo.getMemoById(10L) } returns expected

        // When
        val result = useCase(10L)

        // Then
        assertEquals(expected, result)
    }
}
