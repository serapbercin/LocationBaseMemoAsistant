package com.example.domain.usecases

import com.example.domain.MemoRepositoryApi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DeleteMemoUseCaseTest {

    private val repo: MemoRepositoryApi = mockk()
    private val useCase = DeleteMemoUseCase(repo)

    @Test
    fun `delegates to repository and returns deleted rows count`() = runTest {
        // Given
        coEvery { repo.deleteById(7L) } returns 1

        // When
        val result = useCase(7L)

        // Then
        assertEquals(1, result)
        coVerify(exactly = 1) { repo.deleteById(7L) }
    }
}
