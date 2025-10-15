package com.example.data

import app.cash.turbine.test
import com.example.domain.Memo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MemoRepositoryTest {

    private val dao: MemoDao = mockk(relaxed = true)
    private val db: AppDatabase = mockk {
        every { memoDao() } returns dao
    }
    private val repo = MemoRepository(db)
    private val dispatcher = UnconfinedTestDispatcher()

    @Test
    fun saveMemo_delegates_with_entity_mapping() = runTest(dispatcher) {
        val cap = slot<MemoEntity>()
        coEvery { dao.upsert(capture(cap)) } returns 10L
        val memo = Memo(
            id = null,
            title = "T",
            description = "D",
            reminderDate = 123L,
            reminderLatitude = 1L,
            reminderLongitude = 2L,
            isDone = false
        )
        repo.saveMemo(memo)
        coVerify { dao.upsert(any()) }
        val e = cap.captured
        assertEquals(0L, e.id)
        assertEquals("T", e.title)
        assertEquals("D", e.description)
        assertEquals(123L, e.reminderDate)
        assertEquals(1L, e.reminderLatitude)
        assertEquals(2L, e.reminderLongitude)
        assertFalse(e.isDone)
    }

    @Test
    fun saveMemoAndReturnId_returns_row_id() = runTest(dispatcher) {
        val cap = slot<MemoEntity>()
        coEvery { dao.upsert(capture(cap)) } returns 42L
        val memo = Memo(
            id = null,
            title = "X",
            description = "Y",
            reminderDate = 9L,
            reminderLatitude = 99L,
            reminderLongitude = 100L,
            isDone = true
        )
        val id = repo.saveMemoAndReturnId(memo)
        assertEquals(42L, id)
        val e = cap.captured
        assertEquals("X", e.title)
        assertEquals("Y", e.description)
        assertEquals(9L, e.reminderDate)
        assertEquals(99L, e.reminderLatitude)
        assertEquals(100L, e.reminderLongitude)
        assertTrue(e.isDone)
    }

    @Test
    fun getAll_maps_entities_to_domain() = runTest(dispatcher) {
        val entities = listOf(
            MemoEntity(1L, "A", "a", 1L, 10L, 20L, false),
            MemoEntity(2L, "B", "b", 2L, 30L, 40L, true)
        )
        coEvery { dao.getAll() } returns entities
        val result = repo.getAll()
        assertEquals(2, result.size)
        assertEquals(Memo(1L, "A", "a", 1L, 10L, 20L, false), result[0])
        assertEquals(Memo(2L, "B", "b", 2L, 30L, 40L, true), result[1])
    }

    @Test
    fun getMemoById_maps_entity() = runTest(dispatcher) {
        val entity = MemoEntity(5L, "T", "D", 7L, 8L, 9L, false)
        coEvery { dao.getMemoById(5L) } returns entity
        val m = repo.getMemoById(5L)
        assertEquals(Memo(5L, "T", "D", 7L, 8L, 9L, false), m)
    }

    @Test(expected = NoSuchElementException::class)
    fun getMemoById_throws_when_null() = runTest(dispatcher) {
        coEvery { dao.getMemoById(99L) } returns null
        repo.getMemoById(99L)
    }

    @Test
    fun observeAll_maps_stream() = runTest(dispatcher) {
        val upstream = MutableSharedFlow<List<MemoEntity>>(replay = 0)
        every { dao.observeAll() } returns upstream
        val m1 = MemoEntity(1L, "A", "a", 0L, 1L, 2L, false)
        val m2 = MemoEntity(2L, "B", "b", 0L, 3L, 4L, true)
        repo.observeAll().test {
            upstream.emit(listOf(m1))
            assertEquals(listOf(Memo(1L, "A", "a", 0L, 1L, 2L, false)), awaitItem())
            upstream.emit(listOf(m1, m2))
            assertEquals(
                listOf(
                    Memo(1L, "A", "a", 0L, 1L, 2L, false),
                    Memo(2L, "B", "b", 0L, 3L, 4L, true)
                ),
                awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun updateMemo_returns_count_and_maps_entity() = runTest(dispatcher) {
        val cap = slot<MemoEntity>()
        coEvery { dao.update(capture(cap)) } returns 1
        val memo = Memo(10L, "U", "u", 5L, 6L, 7L, true)
        val count = repo.updateMemo(memo)
        assertEquals(1, count)
        val e = cap.captured
        assertEquals(10L, e.id)
        assertEquals("U", e.title)
        assertEquals("u", e.description)
        assertEquals(5L, e.reminderDate)
        assertEquals(6L, e.reminderLatitude)
        assertEquals(7L, e.reminderLongitude)
        assertTrue(e.isDone)
    }

    @Test
    fun deleteById_returns_count_if_supported() = runTest(dispatcher) {
        coEvery { dao.deleteById(7L) } returns 1
        val count = repo.deleteById(7L)
        assertEquals(1, count)
    }
}
