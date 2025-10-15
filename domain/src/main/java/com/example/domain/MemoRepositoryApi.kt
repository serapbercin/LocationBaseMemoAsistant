package com.example.domain

import kotlinx.coroutines.flow.Flow

/**
 * Interface for a repository offering memo related CRUD operations.
 */
interface MemoRepositoryApi {

    /**
     * Saves the given memo to the database.
     */
    suspend fun saveMemo(memo: Memo)

    /**
     * @return all memos currently in the database.
     */
    suspend fun getAll(): List<Memo>

    /**
     * @return the memo whose id matches the given id.
     */
    suspend fun getMemoById(id: Long): Memo

    suspend fun saveMemoAndReturnId(memo: Memo): Long

    /** Observe all memos as a cold flow that emits on changes. */
    fun observeAll(): Flow<List<Memo>>

    suspend fun updateMemo(memo: Memo): Int

    /** Deletes a memo by id. @return number of rows deleted (0 if none). */
    suspend fun deleteById(id: Long): Int
}