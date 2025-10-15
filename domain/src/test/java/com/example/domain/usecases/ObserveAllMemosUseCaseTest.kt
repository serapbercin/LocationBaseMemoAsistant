package com.example.domain.usecases

import app.cash.turbine.test
import com.example.domain.Memo
import com.example.domain.MemoRepositoryApi
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveAllMemosUseCaseTest {

    private val repo: MemoRepositoryApi = mockk()
    private val useCase = ObserveAllMemosUseCase(repo)

    @Test
    fun `emits lists from repository flow`() = runTest {
        // Given
        val upstream = MutableSharedFlow<List<Memo>>(replay = 0)
        every { repo.observeAll() } returns upstream

        val memoTest = Memo(1L, "A", "a", 0L, 1L, 2L, false)
        val memoTest2 = Memo(2L, "B", "b", 0L, 3L, 4L, true)

        // When / Then
        useCase().test {
            upstream.emit(listOf(memoTest))
            assertEquals(listOf(memoTest), awaitItem())

            upstream.emit(listOf(memoTest, memoTest2))
            assertEquals(listOf(memoTest, memoTest2), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}
