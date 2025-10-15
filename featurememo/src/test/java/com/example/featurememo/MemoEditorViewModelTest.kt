package com.example.featurememo

import app.cash.turbine.test
import com.example.domain.Memo
import com.example.domain.usecases.GetMemoByIdUseCase
import com.example.domain.usecases.SaveMemoUseCase
import com.example.domain.usecases.UpdateMemoUseCase
import com.google.android.gms.maps.model.LatLng
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MemoEditorViewModelTest {

    private val getMemoById: GetMemoByIdUseCase = mockk()
    private val saveMemo: SaveMemoUseCase = mockk()
    private val updateMemo: UpdateMemoUseCase = mockk()

    private val mainDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(mainDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun testViewModel() = MemoEditorViewModel(getMemoById, saveMemo, updateMemo)


    @Test
    fun `initialize(null) sets Create and initialized`() = runTest {
        val vm = testViewModel()

        vm.initialize(null)

        val ui = vm.uiStateFlow.value
        assertTrue(ui.initialized)
        assertTrue(ui.mode is EditorMode.Create)
        assertEquals("", ui.title)
        assertEquals("", ui.description)
        assertNull(ui.location)
    }

    @Test
    fun `initialize id loads memo and maps fields`() = runTest {
        val vm = testViewModel()
        val memo = Memo(
            id = 7L,
            title = "T",
            description = "D",
            reminderDate = 123L,
            reminderLatitude = (52.520008 * 1e7).toLong(),
            reminderLongitude = (13.404954 * 1e7).toLong(),
            isDone = false
        )
        coEvery { getMemoById(7L) } returns memo

        vm.initialize(7L)
        advanceUntilIdle()

        val ui = vm.uiStateFlow.value
        assertTrue(ui.initialized)
        assertTrue(ui.mode is EditorMode.Edit)
        assertEquals("T", ui.title)
        assertEquals("D", ui.description)
        assertNotNull(ui.location)
        assertEquals(52.520008, ui.location!!.latitude, 1e-6)
        assertEquals(13.404954, ui.location!!.longitude, 1e-6)
    }

    @Test
    fun `initialize is idempotent (second call is ignored)`() = runTest {
        val vm = testViewModel()
        vm.initialize(null)
        val first = vm.uiStateFlow.value
        vm.initialize(999L)
        val second = vm.uiStateFlow.value
        assertSame(first, second)
    }

    @Test
    fun `title, description, location update state and isValid`() = runTest {
        val vm = testViewModel()
        vm.initialize(null)

        assertFalse(vm.isValid())

        vm.onTitleChanged("Hello")
        vm.onDescriptionChanged("World")
        vm.onLocationChanged(LatLng(1.0, 2.0))

        val ui = vm.uiStateFlow.value
        assertEquals("Hello", ui.title)
        assertEquals("World", ui.description)
        assertEquals(1.0, ui.location!!.latitude, 0.0)
        assertEquals(2.0, ui.location!!.longitude, 0.0)
        assertTrue(vm.isValid())
    }

    @Test
    fun `submit in Create calls save and onCreated, resets state`() = runTest {
        val vm = testViewModel()
        vm.initialize(null)
        vm.onTitleChanged("T")
        vm.onDescriptionChanged("D")
        vm.onLocationChanged(LatLng(10.0, 20.0))

        coEvery { saveMemo("T", "D", 10.0, 20.0, any()) } returns 42L

        var createdId: Long? = null
        vm.submit(
            onCreated = { createdId = it },
            onUpdated = {}
        )
        advanceUntilIdle()

        assertEquals(42L, createdId)
        val ui = vm.uiStateFlow.value
        assertTrue(ui.initialized)
        assertTrue(ui.mode is EditorMode.Create)
    }

    @Test
    fun `submit in Create with missing location does nothing`() = runTest {
        val vm = testViewModel()
        vm.initialize(null)
        vm.onTitleChanged("T")
        vm.onDescriptionChanged("D")

        var createdCalled = false
        vm.submit(
            onCreated = { createdCalled = true },
            onUpdated = {}
        )
        advanceUntilIdle()

        assertFalse(createdCalled)
    }

    @Test
    fun `submit in Edit when update returns true calls onUpdated`() = runTest {
        val vm = testViewModel()
        val repoMemo = Memo(
            id = 5L, title = "Old", description = "Old",
            reminderDate = 0L,
            reminderLatitude = 0L, reminderLongitude = 0L, isDone = false
        )
        coEvery { getMemoById(5L) } returns repoMemo
        vm.initialize(5L)
        advanceUntilIdle()

        vm.onTitleChanged("New")
        vm.onDescriptionChanged("Body")
        vm.onLocationChanged(LatLng(1.0, 2.0))

        coEvery { updateMemo(5L, "New", "Body", 1.0, 2.0, any()) } returns true

        var updatedCalled = false
        vm.submit(
            onCreated = {},
            onUpdated = { updatedCalled = true }
        )
        advanceUntilIdle()

        assertTrue(updatedCalled)
    }

    @Test
    fun `submit in Edit when update returns false emits ShowMessage`() = runTest(UnconfinedTestDispatcher()) {
        val vm = testViewModel()
        val repoMemo = Memo(
            id = 5L, title = "Old", description = "Old",
            reminderDate = 0L,
            reminderLatitude = 0L, reminderLongitude = 0L, isDone = false
        )
        coEvery { getMemoById(5L) } returns repoMemo
        vm.initialize(5L)
        advanceUntilIdle()

        vm.onTitleChanged("New")
        vm.onDescriptionChanged("Body")
        vm.onLocationChanged(LatLng(1.0, 2.0))

        coEvery { updateMemo(5L, "New", "Body", 1.0, 2.0, any()) } returns false

        vm.effects.test {
            vm.submit(onCreated = {}, onUpdated = {})
            val eff = awaitItem()
            assertTrue(eff is Effect.ShowMessage)
            val show = eff as Effect.ShowMessage
            assertEquals(R.string.msg_update_failed, show.resId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `submit in Edit when update throws emits ShowMessage`() = runTest(UnconfinedTestDispatcher()) {
        val vm = testViewModel()
        val repoMemo = Memo(
            id = 5L, title = "Old", description = "Old",
            reminderDate = 0L,
            reminderLatitude = 0L, reminderLongitude = 0L, isDone = false
        )
        coEvery { getMemoById(5L) } returns repoMemo
        vm.initialize(5L)
        advanceUntilIdle()

        vm.onTitleChanged("New")
        vm.onDescriptionChanged("Body")
        vm.onLocationChanged(LatLng(1.0, 2.0))

        coEvery { updateMemo(5L, "New", "Body", 1.0, 2.0, any()) } throws RuntimeException("db")

        vm.effects.test {
            vm.submit(onCreated = {}, onUpdated = {})
            val eff = awaitItem()
            assertTrue(eff is Effect.ShowMessage)
            val show = eff as Effect.ShowMessage
            assertEquals(R.string.msg_update_failed, show.resId)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
