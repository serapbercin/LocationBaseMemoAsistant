package com.example.featurememo

import app.cash.turbine.test
import com.example.domain.Memo
import com.example.domain.usecases.DeleteMemoUseCase
import com.example.domain.usecases.ObserveAllMemosUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val observeMemos: ObserveAllMemosUseCase = mockk()
    private val deleteMemo: DeleteMemoUseCase = mockk()
    private val dispatcher = UnconfinedTestDispatcher()

    @Before fun setup() = Dispatchers.setMain(dispatcher)
    @After fun tearDown() = Dispatchers.resetMain()

    private fun testViewModel() = HomeViewModel(observeMemos, deleteMemo)

    @Test
    fun `state starts loading then shows memos`() = runTest(dispatcher) {
        val upstream = MutableSharedFlow<List<Memo>>(replay = 0)
        every { observeMemos() } returns upstream
        val vm = testViewModel()
        val job = launch { vm.state.collect {} }
        advanceUntilIdle()
        val first = vm.state.value
        assertTrue(first.isLoading)
        val m1 = Memo(1L, "A", "a", 0L, 1L, 2L, false)
        upstream.emit(listOf(m1))
        advanceUntilIdle()
        val next = vm.state.value
        assertFalse(next.isLoading)
        assertEquals(listOf(m1), next.memos)
        job.cancel()
    }

    @Test
    fun `state emits error when upstream throws`() = runTest(dispatcher) {
        every { observeMemos() } returns flow { throw RuntimeException("exception") }
        val vm = testViewModel()
        val job = launch { vm.state.collect {} }
        advanceUntilIdle()
        val second = vm.state.value
        assertFalse(second.isLoading)
        assertTrue(second.memos.isEmpty())
        assertEquals("exception", second.error)
        job.cancel()
    }

    @Test
    fun `onAddClicked emits NavigateToEdit null`() = runTest(dispatcher) {
        every { observeMemos() } returns MutableSharedFlow()
        val vm = testViewModel()
        vm.effects.test {
            vm.onAddClicked()
            val effect = awaitItem()
            assertTrue(effect is HomeViewModel.Effect.NavigateToEdit)
            assertNull((effect as HomeViewModel.Effect.NavigateToEdit).id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onMemoClicked emits NavigateToEdit with id`() = runTest(dispatcher) {
        every { observeMemos() } returns MutableSharedFlow()
        val vm = testViewModel()
        vm.effects.test {
            vm.onMemoClicked(5L)
            val effect = awaitItem()
            assertTrue(effect is HomeViewModel.Effect.NavigateToEdit)
            assertEquals(5L, (effect as HomeViewModel.Effect.NavigateToEdit).id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDeleteClicked success emits ShowMessage success`() = runTest(dispatcher) {
        every { observeMemos() } returns MutableSharedFlow()
        coEvery { deleteMemo(7L) } returns 1
        val vm = testViewModel()
        vm.effects.test {
            vm.onDeleteClicked(7L)
            val effect = awaitItem()
            assertTrue(effect is HomeViewModel.Effect.ShowMessage)
            assertEquals(R.string.msg_delete_success, (effect as HomeViewModel.Effect.ShowMessage).resId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDeleteClicked failure emits ShowMessage failed`() = runTest(dispatcher) {
        every { observeMemos() } returns MutableSharedFlow()
        coEvery { deleteMemo(9L) } throws RuntimeException()
        val vm = testViewModel()
        vm.effects.test {
            vm.onDeleteClicked(9L)
            val effect = awaitItem()
            assertTrue(effect is HomeViewModel.Effect.ShowMessage)
            assertEquals(R.string.msg_delete_failed, (effect as HomeViewModel.Effect.ShowMessage).resId)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
